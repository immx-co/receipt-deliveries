FROM maven:3.9.16-eclipse-temurin-26 AS builder

WORKDIR /workspace

COPY . .

RUN mvn --batch-mode \
    -pl receipt-deliveries-frontend \
    -am \
    clean package \
    -DskipTests \
    -Dvaadin.productionMode=true

FROM eclipse-temurin:26.0.2_10-jre-alpine

ARG APP_VERSION=dev
ARG VCS_REF=unknown

LABEL org.opencontainers.image.title="Receipt Deliveries Frontend"
LABEL org.opencontainers.image.description="Vaadin frontend сервиса регистрации поставок"
LABEL org.opencontainers.image.version="${APP_VERSION}"
LABEL org.opencontainers.image.revision="${VCS_REF}"
LABEL org.opencontainers.image.source="https://github.com/immx-co/receipt-deliveries"

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder --chown=spring:spring \
    /workspace/receipt-deliveries-frontend/target/receipt-deliveries-frontend-*.jar \
    app.jar

USER spring:spring

ENV VAADIN_LAUNCH_BROWSER=false

EXPOSE 3200

ENTRYPOINT ["java", "-jar", "app.jar"]