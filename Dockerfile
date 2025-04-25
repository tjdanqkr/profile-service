FROM openjdk:17-slim AS builder
LABEL stage="builder"
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test


FROM openjdk:17-slim
LABEL authors="qkrtjdan"
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "if [ -z \"$SPRING_DATASOURCE_URL\" ] || [ -z \"$SPRING_DATASOURCE_USERNAME\" ] || [ -z \"$SPRING_DATASOURCE_PASSWORD\" ]; then echo 'Missing required environment variables: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD' && exit 1; fi && java -jar /app/app.jar"]

