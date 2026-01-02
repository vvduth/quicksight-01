## intro
* today I will try to dockerize a laravel and php project, 
* I will try to run the app only using docker containers
* I do not have any laravel or php installed on my machine and also I do not have any experience with php and laravel
* The reason is I want to emphasize docker can be use to run any app regardless of the tech stack
* I will document the steps I take to dockerize the app in this file

* for more , Laravel and PHP that requires

quite a complex setup on your machine. with some tech like node js, you can just install node and npm and run the app. but with laravel and php, you need to install php, composer, a web server like apache or nginx, and a database like mysql or postgresql. so dockerizing the app will make it easier to run without worrying about installing all these dependencies on my machine.

## what so special about laravel and php
* if we explore laravel doc, there iare quite alot of requirements to run a laravel app
* we need php, and php is still not enough
* bback to compare to node js, it is not only the language in which we write the code, but also the runtime that runs the code, build the server that handles the request, and also the package manager that manages the dependencies. so we have applcaituon code and the server logic all in one place.
* with php and laravel, we cant build the server with php alone, Instead, we need a server that handles incoming requests

which then triggers the PHP interpreter

to run our PHP code for these incoming requests.

And setting all of that up on your local machine

possibly combined with MySQL or

MongoDB databases, that can be quite annoying.

## target set up
* we have some folr in our host machine that have the laravel app code (source code)
* this folder will be expose to one containrt which as php interpreter installed
*  we also need and extra server container whuch has nginx will take incoming requests and forward them to the php container to generate the response and return it to the client
*  for stroing data, we will use mysql container
*  php container need to be able to connect to mysql container to read and write data
=> these 3 are application containers

### ultility container:
* Because it turns out that in Laravel applications,

there are three kinds of tools, utilities, which we need.

For example, we need Composer. => composer is the package manager for php, similar to npm for node js
* in additon laravel ships with it owns tools called artisan => artisan is a command line tool that helps us to do common tasks like creating migration, seeding database, etc
* so these two tools are needed to manage the laravel app
* And last but not least, we will also use NPM here.

It's not that important but Laravel uses it

for some of its front end logic.

So if in your views, which are returned by Laravel

you need some Java script code, for example.

### over all:
* we need 6 containers in total
  * 3 application containers: php, nginx, mysql
  * 3 utility containers: composer, artisan, npm
  
## let start dockerizing

## make sure you have docker and docker compose installed
* you can follow the instruction from docker official doc to install docker and docker compose on your machine


### step1 : create docker-compose.yaml file
* declare 6 docker services in the docker-compose.yaml file
* ![services in laravel app](image.png)


### server containre with nginx
* we will use the official nginx image from docker hub
* expose to port 8000 on host machine
```yaml
servers:
    image: 'nginx:stable-alpine'
    ports:
      - "8000:80"
```
* export to port 8000 on our host machine and forward it to port 80 in the container
* after the bind mount the voulme to the container, we need to copy the nginx config file to the container
  ```yaml
  volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      ```
- explain: we have a folder called nginx in our host machine, which contains the nginx config file
- we mount this file to the container's nginx config file path
- follow the nginx doc to create the config file

create the nginx/nginx.conf file in our host machine
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
- explain: in the config file, we set the root directory to /var/www/html/public, because in laravel app, the public folder is the entry point of the app, listen on port 80, handle request to index.php file, and forward php request to the php container on port 3000
