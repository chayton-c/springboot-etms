FROM openjdk:11
VOLUME /tmp
ADD springboot-demo-1.0.0.jar /test.jar
EXPOSE 80
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]