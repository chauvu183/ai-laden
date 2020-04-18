FROM openjdk:13-jdk-alpine

VOLUME /tmp

RUN apk add --no-cache curl tar bash procps
RUN wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
RUN chmod +x cloud_sql_proxy

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ARG TOKEN_FILE
COPY ${TOKEN_FILE} token.json

COPY wrapper-startprocesses-docker.sh .
RUN chmod +x wrapper-startprocesses-docker.sh

ENV INSTANCE default

CMD [ "sh", "-c", "./wrapper-startprocesses-docker.sh $INSTANCE" ]
