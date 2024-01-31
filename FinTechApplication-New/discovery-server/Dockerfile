FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/discovery-server-3.2.2.jar /app/app.jar

EXPOSE 8761

CMD ["java", "-jar", "app.jar"]
