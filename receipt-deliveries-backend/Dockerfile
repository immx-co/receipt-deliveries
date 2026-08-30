FROM maven:3.9.16-eclipse-temurin-26-alpine AS builder

WORKDIR /workspace

COPY . .

RUN mvn --batch-mode -pl receipt-deliveries-backend -am clean package -DskipTests

FROM eclipse-temurin:26.0.2_10-jre-alpine

ARG APP_VERSION=dev
ARG VCS_REF=unknown

LABEL org.opencontainers.image.title="Receipt Deliveries Backend"
LABEL org.opencontainers.image.description="Backend сервиса регистрации поставок"
LABEL org.opencontainers.image.version="${APP_VERSION}"
LABEL org.opencontainers.image.revision="${VCS_REF}"
LABEL org.opencontainers.image.source="https://github.com/immx-co/receipt-deliveries"

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring /workspace/receipt-deliveries-backend/target/receipt-deliveries-backend-*.jar app.jar

USER spring:spring

EXPOSE 3100

ENTRYPOINT ["java", "-jar", "app.jar"]