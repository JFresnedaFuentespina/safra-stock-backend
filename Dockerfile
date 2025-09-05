########################
# 1) BUILD STAGE
########################
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /workspace

# Descarga dependencias primero (aprovecha caché)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copia código fuente y compila
COPY src ./src
RUN mvn clean package -DskipTests

########################
# 2) RUNTIME STAGE
########################
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
