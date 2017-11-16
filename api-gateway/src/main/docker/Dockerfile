FROM frolvlad/alpine-oraclejdk8:slim

ADD api-gateway-server.jar /usr/local/api-gateway-server/

WORKDIR /usr/local/api-gateway-server/

CMD ["java", "-jar", "api-gateway-server.jar"]