
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY customer-rewards-api/pom.xml .
COPY customer-rewards-api/src/ ./src/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/customer-rewards-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar app.jar"]
