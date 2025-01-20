FROM openjdk:17.0.1-jdk-slim


# Install dockerize
RUN apt-get update && apt-get install -y wget
RUN wget https://github.com/jwilder/dockerize/releases/download/v0.9.2/dockerize-linux-amd64-v0.9.2.tar.gz
RUN tar -xzvf dockerize-linux-amd64-v0.9.2.tar.gz -C /usr/local/bin

ADD target/backend.jar /app/backend.jar

ENTRYPOINT ["dockerize", "-wait", "tcp://rabbitmq:5672", "-timeout", "60s", "java", "-jar", "/app/backend.jar"]