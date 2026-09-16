# media-service/Dockerfile
FROM gradle:8.14-jdk21 AS builder

WORKDIR /app
COPY . .
RUN gradle :user-service:quarkusBuild -x test --no-daemon --parallel

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
RUN addgroup --system --gid 1000 appgroup && \
    adduser --system --uid 1000 --gid 1000 appuser

RUN mkdir -p /var/log/media-service && \
    chown -R appuser:appgroup /var/log/media-service && \
    chmod 755 /var/log/media-service

COPY --from=builder /app/media-service/build/quarkus-app /app

USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx200m", "-jar", "quarkus-run.jar"]