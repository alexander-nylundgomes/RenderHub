FROM openjdk:17.0.1-jdk-slim


# Install dockerize
RUN apt-get update && apt-get install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.9.2/dockerize-linux-amd64-v0.9.2.tar.gz
RUN tar -xzvf dockerize-linux-amd64-v0.9.2.tar.gz -C /usr/local/bin

# Install imagemagick
# RUN apt-get update && apt-get install -y imagemagick libmagickwand-dev --no-install-recommends
# RUN pecl install imagick
# RUN docker-php-ext-enable imagick

RUN apt-get update && apt-get install -y imagemagick

# RUN apt-get update && apt-get install -y \
#     imagemagick libmagickwand-dev --no-install-recommends \
#     && pecl install imagick \
#     && docker-php-ext-enable imagick

ADD target/consumer.jar /app/consumer.jar

ENTRYPOINT ["dockerize", "-wait", "tcp://backend:8080", "-timeout", "60s", "java", "-jar", "/app/consumer.jar"]