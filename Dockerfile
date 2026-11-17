FROM gradle:9.7.1-jdk21 AS build

WORKDIR /workspace

COPY --chown=gradle:gradle gradle/ gradle/
COPY --chown=gradle:gradle gradlew build.gradle settings.gradle ./
COPY --chown=gradle:gradle src/ src/

RUN chmod +x gradlew \
    && ./gradlew --no-daemon bootJar \
    && jar_file="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" \
    && test -n "$jar_file" \
    && cp "$jar_file" /tmp/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build --chown=10001:10001 /tmp/app.jar /app/app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
