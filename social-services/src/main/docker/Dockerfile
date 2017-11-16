FROM frolvlad/alpine-oraclejdk8:slim

ADD social-service.jar /usr/local/social-service/

WORKDIR /usr/local/social-service/

CMD ["java", "-jar", "social-service.jar"]