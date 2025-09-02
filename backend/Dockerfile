FROM eclipse-temurin:17-jdk-alpine

# Create app directory
RUN mkdir /opt/app
WORKDIR /opt/app

COPY target/DailyVerse-0.0.1-SNAPSHOT.jar /opt/app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "-Dspring.config.location=application.properties"]
