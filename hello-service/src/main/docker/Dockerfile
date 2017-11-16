FROM frolvlad/alpine-oraclejdk8:slim

ADD hello-service.jar /usr/local/hello-service/

WORKDIR /usr/local/hello-service/

CMD ["java", "-jar", "hello-service.jar"]