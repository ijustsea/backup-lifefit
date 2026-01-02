FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY app.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","app.jar", "--spring.profiles.active=prod"]