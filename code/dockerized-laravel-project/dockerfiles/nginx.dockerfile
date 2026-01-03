FROM nginx:stable-alpine

WORKDIR /etc/nginx/conf.d

COPY nginx/nginx.conf .

# in local folder the file naem is nginx.conf , but in container it is default.conf
RUN mv nginx.conf default.conf 

WORKDIR /var/www/html

COPY src .