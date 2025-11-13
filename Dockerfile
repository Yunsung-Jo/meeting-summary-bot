# 1. 빌드 스테이지
FROM gradle:8-jdk21 AS builder

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew shadowJar

# 2. 실행 스테이지
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
RUN apk --no-cache add curl
COPY --from=builder /app/build/libs/*all.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
