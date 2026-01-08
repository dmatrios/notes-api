# ===== STAGE 1: build =====
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiamos solo lo necesario para cachear dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Ahora sí copiamos el código
COPY src src

RUN ./mvnw clean package -DskipTests


# ===== STAGE 2: runtime =====
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
