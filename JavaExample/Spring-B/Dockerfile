FROM gradle:8.8-jdk21 AS build

WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 80

# Запуск приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
