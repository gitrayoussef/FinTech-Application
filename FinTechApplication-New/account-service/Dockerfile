FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/account-service-3.2.2.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
