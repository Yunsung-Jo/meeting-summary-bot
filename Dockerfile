# 1. 빌드 스테이지
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew shadowJar --no-daemon

# 2. 실행 스테이지
FROM eclipse-temurin:25-jre

WORKDIR /app
RUN apt-get update && apt-get install -y libopus0 libopus-dev libc6 libstdc++6 curl && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/build/libs/*all.jar app.jar

ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "app.jar"]
