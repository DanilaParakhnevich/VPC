FROM gradle:8.14-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle :gateway-service:bootJar -x test --no-daemon --parallel

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system --gid 1000 appgroup && \
    adduser --system --uid 1000 --gid 1000 appuser

RUN mkdir -p /var/log/gateway-service && \
    chown -R appuser:appgroup /var/log/gateway-service && \
    chmod 755 /var/log/gateway-service

COPY --from=builder /app/gateway-service/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx200m", "-jar", "app.jar"]