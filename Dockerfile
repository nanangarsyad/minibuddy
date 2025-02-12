FROM gradle:8.12-jdk-21-and-23-alpine AS builder
WORKDIR /app
RUN apk update && apk add ffmpeg
COPY . .
RUN gradle build --no-daemon

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
RUN apk update && apk add ffmpeg
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
