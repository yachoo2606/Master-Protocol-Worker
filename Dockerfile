FROM maven:3.9.5-eclipse-temurin-17-alpine as builder
LABEL authors="Jan Biały - yachoo2606"

# Set the working directory in the container
WORKDIR /app
COPY src src
COPY .mvn .mvn
COPY ./mvnw mvnw
COPY ./mvnw.cmd mvnw.cmd
COPY ./pom.xml pom.xml

RUN mvn clean install

FROM openjdk:17-alpine3.14

COPY --from=builder /app/target/*.jar /app/executables/Worker-0.0.1-SNAPSHOT.jar

WORKDIR /app/executables

# Specify the command to run on container startup
CMD ["java", "-jar", "Worker-0.0.1-SNAPSHOT.jar"]