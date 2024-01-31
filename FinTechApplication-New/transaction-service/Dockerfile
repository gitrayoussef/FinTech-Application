FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/transaction-service-3.2.2.jar /app/app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
