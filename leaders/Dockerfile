# using multistage docker build
# ref: https://docs.docker.com/develop/develop-images/multistage-build/

# temp container to build using gradle
FROM openjdk:21-jdk AS build
ENV APP_HOME=/app/
WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN ./gradlew build || return 0
COPY . .
RUN ./gradlew clean bootJar

# actual container
FROM openjdk:21-jdk-slim as production
ENV ARTIFACT_NAME=leaders-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/app/

WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$ARTIFACT_NAME .

EXPOSE 9092
LABEL org.opencontainers.image.source=https://github.com/jakejack13/speed
LABEL org.opencontainers.image.description="leaders image"
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}
