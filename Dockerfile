FROM openjdk:17-slim AS builder
LABEL stage="builder"
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build


FROM openjdk:17-slim
LABEL authors="qkrtjdan"
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","-DSPRING_PROFILE_ACTIVE=DEV" ,"/app/app.jar"]

