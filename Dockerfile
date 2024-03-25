FROM eclipse-temurin:21-jre-alpine
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]