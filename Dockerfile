FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 파일들만 먼저 복사
COPY build.gradle settings.gradle /app/
COPY core/build.gradle /app/core/
COPY api/build.gradle /app/api/
COPY infra/build.gradle /app/infra/
COPY bootstrap/build.gradle /app/bootstrap/

# 의존성 다운로드
RUN gradle build --build-cache || return 0

# 전체 소스 코드 복사
COPY . /app/

RUN gradle :bootstrap:bootJar

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/bootstrap/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]