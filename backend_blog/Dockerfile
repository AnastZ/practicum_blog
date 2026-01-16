
FROM maven:3.9.6-eclipse-temurin-21 AS build

RUN apt-get update && apt-get install -y dos2unix

WORKDIR /app

COPY src ./src
COPY pom.xml ./

RUN mvn clean package

FROM tomcat:10.1.50-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*


COPY --from=build /app/target/backend_blog.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8082
CMD ["catalina.sh", "run"]

