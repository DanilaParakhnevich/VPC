FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle* gradle.properties /app/
RUN gradle dependencies --no-daemon
COPY src /app/src
RUN gradle clean
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN addgroup --system --gid 1000 appgroup && \
    adduser --system --uid 1000 --gid 1000 appuser
COPY --from=builder /app/build/libs/*.jar app.jar
RUN mkdir -p /var/log/user-service && chown appuser:appgroup /var/log/user-service
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]