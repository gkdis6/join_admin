# 빌드 스테이지
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Gradle Wrapper 및 빌드 설정 파일들 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드만 먼저 실행 (소스 변경 시 캐시 재사용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드는 마지막에 복사 (소스 변경 시에만 재빌드)
COPY src src/

# 실제 애플리케이션 빌드
RUN ./gradlew bootJar -x test --no-daemon

# 실행 스테이지
FROM openjdk:17-jdk-slim

VOLUME /tmp
COPY --from=builder /app/build/libs/join-admin-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]