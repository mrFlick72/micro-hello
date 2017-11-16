FROM frolvlad/alpine-oraclejdk8:slim

ADD eureka-server.jar /usr/local/eureka-server/

WORKDIR /usr/local/eureka-server/

EXPOSE 8761

CMD ["java", "-jar", "eureka-server.jar"]