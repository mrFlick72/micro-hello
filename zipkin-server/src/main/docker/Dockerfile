FROM frolvlad/alpine-oraclejdk8:slim

ADD zipkin-server.jar /usr/local/zipkin-server/

WORKDIR /usr/local/zipkin-server/

CMD ["java", "-jar", "zipkin-server.jar"]