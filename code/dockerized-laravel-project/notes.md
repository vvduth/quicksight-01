## Intro
* Today, I'm attempting to dockerize a Laravel and PHP project.
* The goal: run the entire app using *only* Docker containers.
* I have zero Laravel or PHP installed on my machine, and honestly, zero experience with them.
* Why? To prove that Docker can handle any tech stack, keeping my machine clean.
* I'll document every step here.

For context, setting up Laravel and PHP locally is... a lot. Unlike Node.js where you just install Node and run `npm start`, PHP requires the language, a package manager (Composer), a web server (Apache or Nginx), and a database (MySQL/PostgreSQL). Dockerizing this saves me from that installation headache.

## What's so special about Laravel and PHP?
* Exploring the Laravel docs reveals a laundry list of requirements.
* PHP alone isn't enough.
* Unlike Node.js, which handles the code, runtime, and server logic all in one, PHP needs a separate web server to handle incoming requests.
* This server then pokes the PHP interpreter to run the code.
* Setting this up locally, along with a database, can be quite annoying.

## Target Setup
* **Source Code**: A folder on the host machine containing the Laravel app.
* **PHP Container**: Has the PHP interpreter and access to the source code.
* **Nginx Container**: The web server. It takes requests and forwards them to the PHP container.
* **MySQL Container**: Stores our data.
* **Connectivity**: The PHP container needs to talk to the MySQL container.

These form our **3 Application Containers**.

### Utility Containers
Laravel needs tools to function:
1.  **Composer**: The PHP package manager (think npm for PHP).
2.  **Artisan**: Laravel's command-line tool for tasks like database migrations.
3.  **NPM**: Yes, even Laravel uses it for some frontend logic.

### Overall
We need **6 containers** in total:
*   3 Application: PHP, Nginx, MySQL
*   3 Utility: Composer, Artisan, NPM

## Let's Start Dockerizing

### Prerequisites
Make sure you have Docker and Docker Compose installed. Follow the official docs if you haven't.

### Step 1: Create `docker-compose.yaml`
We'll declare our 6 services here.
![services in laravel app](image.png)

### Server Container (Nginx)
We'll use the official Alpine image and expose port 8000.

```yaml
server:
    image: 'nginx:stable-alpine'
    ports:
      - "8000:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro
```

*   **Explanation**: We map port 8000 on the host to port 80 in the container. We also mount our local config file.

Create `nginx/nginx.conf` on the host:

```nginx
server {
    listen 80;
    index index.php index.html;
    server_name localhost;
    root /var/www/html/public;
    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }
    location ~ \.php$ {
        try_files $uri =404;
        fastcgi_split_path_info ^(.+\.php)(/.+)$;
        fastcgi_pass php:3000;
        fastcgi_index index.php;
        include fastcgi_params;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
        fastcgi_param PATH_INFO $fastcgi_path_info;
    }
}
```

*   **Explanation**: We set the root to `/var/www/html/public` (Laravel's entry point). It listens on port 80 and forwards `.php` requests to the `php` container on port 3000.

### PHP Container
We need a custom image to install specific PHP extensions required by Laravel.
Create `dockerfiles/php.dockerfile`:

```dockerfile
FROM php:7.4-fpm-alpine

WORKDIR /var/www/html

RUN docker-php-ext-install pdo pdo_mysql
```

*   **Explanation**: Based on PHP 7.4 FPM Alpine. We install `pdo` and `pdo_mysql` for database connectivity. No `ENTRYPOINT` is needed; it defaults to `php-fpm`.

Update `docker-compose.yaml`:

```yaml
php:
    build:
      context: ./dockerfiles
      dockerfile: php.dockerfile
    volumes:
      - ./src:/var/www/html:delegated
```

*   **Explanation**: We build from our custom Dockerfile and bind mount the `./src` folder so the container can see our code.
*   **Note**: PHP-FPM usually listens on port 9000. If we want it on 3000 (as referenced in Nginx config), we'd need to configure that, or just update Nginx to point to 9000. Let's assume we update Nginx:
    ```nginx
    fastcgi_pass php:9000;
    ```
![php container](image-1.png)

### MySQL Container
Standard setup using the official image.

```yaml
mysql:
    image: 'mysql:5.7'
    env_file:
      - ./env/mysql.env
```

### Composer Container
We need a custom image to run Composer commands.

```dockerfile
FROM composer:latest

WORKDIR /var/www/html

ENTRYPOINT [ "composer", "--ignore-platform-reqs" ]
```

*   **Explanation**: Uses the latest Composer image. The entrypoint runs `composer` ignoring platform requirements (since we're in a container).

Add to `docker-compose.yaml`:

```yaml
composer:
    build:
      context: ./dockerfiles
      dockerfile: composer.dockerfile
    volumes:
      - ./src:/var/www/html:delegated
```

### Create a Laravel App
Run the composer container to scaffold the app:

```bash
docker-compose run --rm composer create-project --prefer-dist laravel/laravel .
```

*   **Explanation**: This runs `create-project` inside the container, depositing the files into `./src` on your host.
![host machine laravel app](image-2.png)

### Running the App
Check `src/.env` for database config and update it to match your MySQL container settings:

```env
DB_CONNECTION=mysql
DB_HOST=mysql
DB_PORT=3306
...
```

Ensure the Nginx container also sees the source code:

```yaml
server:
    # ... other config
    volumes:
      - ./src:/var/www/html:delegated
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro
```

Spin it up:

```bash
docker-compose up -d server php mysql
```

Visit `http://localhost:8000`.

## Encountered Issues & Fixes
I hit a permission error: `file_put_contents(...): Failed to open stream: Permission denied`.
![error 500](image-3.png)

This happens because the container user doesn't have write permissions to the bind-mounted folders on Linux/WSL. Also, Laravel 12+ requires PHP 8.4.

**The Fix:**
Update `dockerfiles/php.dockerfile` to handle permissions and use a newer PHP version:

```dockerfile
FROM php:8.4-fpm-alpine
 
WORKDIR /var/www/html
 
COPY src .
 
RUN docker-php-ext-install pdo pdo_mysql
 
# Create a user 'laravel' to match the host user ID (often 1000)
RUN addgroup -g 1000 laravel && adduser -G laravel -g laravel -s /bin/sh -D laravel
 
USER laravel
```

Update `dockerfiles/composer.dockerfile` similarly:

```dockerfile
FROM composer:latest
 
RUN addgroup -g 1000 laravel && adduser -G laravel -g laravel -s /bin/sh -D laravel
 
USER laravel 
WORKDIR /var/www/html
 
# Switch to root if needed for specific ops, but default to laravel
# USER root 
 
ENTRYPOINT [ "composer", "--ignore-platform-reqs" ]
```

Finally, run migrations using the PHP container (or a dedicated artisan container):

```bash
docker-compose exec php php artisan migrate
```

## Working App
Success!
![working laravel app](image-4.png)

I changed `src/resources/views/welcome.blade.php` and the changes reflected instantly.
![code change refelected](image-5.png)

## Artisan Container
We can reuse the `php.dockerfile` for Artisan commands.

```yaml
artisan:
    build:
      context: ./dockerfiles
      dockerfile: php.dockerfile
    volumes:
      - ./src:/var/www/html
    entrypoint: ["php", "/var/www/html/artisan"]
```

## NPM Container
Simple setup for frontend assets.

```yaml
npm:
    image: 'node:18-alpine'
    working_dir: /var/www/html
    entrypoint: ["npm"]
    volumes:
      - ./src:/var/www/html
```

## Bind Mounts vs. Copy
*   **Bind Mounts**: Great for development. Changes on your host are instantly seen in the container.
*   **Copy**: Better for production. You bake the code into the image.

For a production-like Nginx image (`dockerfiles/nginx.dockerfile`):

```dockerfile
FROM nginx:stable-alpine

WORKDIR /etc/nginx/conf.d
COPY nginx/nginx.conf .
RUN mv nginx.conf default.conf 

WORKDIR /var/www/html
COPY src .
```

Update `docker-compose.yaml` to build it:

```yaml
server:
    build:
      context: .
      dockerfile: dockerfiles/nginx.dockerfile
    ports:
      - "8080:80"
    depends_on:
      - php
      - mysql
```