# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

# Gradle 설치
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.5-bin.zip && \
    unzip gradle-8.5-bin.zip && \
    mv gradle-8.5 /opt/gradle && \
    rm gradle-8.5-bin.zip

ENV PATH="/opt/gradle/bin:${PATH}"

WORKDIR /app
COPY . .
RUN gradle bootJar -x test

# 실행 스테이지
FROM openjdk:17-jdk-slim

VOLUME /tmp
COPY --from=builder /app/build/libs/join-admin-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]