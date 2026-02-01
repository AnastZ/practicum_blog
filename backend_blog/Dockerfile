
FROM gradle:8.7.0-jdk21 AS build


WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

COPY src ./src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/app.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]