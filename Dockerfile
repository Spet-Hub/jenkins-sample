FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY src ./src

RUN mkdir -p out \
    && javac -d out $(find src/main/java -name "*.java") \
    && cp -R src/main/resources/. out/

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/out ./out

ENV PORT=18888

CMD ["java", "-cp", "out", "com.example.jenkinssample.App"]
