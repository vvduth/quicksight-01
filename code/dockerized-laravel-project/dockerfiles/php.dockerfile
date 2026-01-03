FROM php:8.4-fpm-alpine
 
WORKDIR /var/www/html
 
COPY src .
 
RUN docker-php-ext-install pdo pdo_mysql
 
# IF YOU GET PERMISSIONS ISSUES ON /var/www/html/storage
# RUN chown -R www-data:www-data .
 
RUN addgroup -g 1000 laravel && adduser -G laravel -g laravel -s /bin/sh -D laravel
 
USER laravel