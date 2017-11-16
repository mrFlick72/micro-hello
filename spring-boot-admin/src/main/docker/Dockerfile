FROM frolvlad/alpine-oraclejdk8:slim

ADD spring-boot-admin.jar /usr/local/spring-boot-admin/

WORKDIR /usr/local/spring-boot-admin/

CMD ["java", "-jar", "spring-boot-admin.jar"]