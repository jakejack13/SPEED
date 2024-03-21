# using multistage docker build
# ref: https://docs.docker.com/develop/develop-images/multistage-build/

# temp container to build using gradle
FROM eclipse-temurin:21.0.2_13-jdk AS build
ENV APP_HOME=/app/
WORKDIR $APP_HOME

COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN ./gradlew build || return 0
COPY . .
RUN ./gradlew clean check
RUN ./gradlew clean bootJar

# actual container
FROM eclipse-temurin:21.0.2_13-jre-alpine as production
ENV ARTIFACT_NAME=workers-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/app/

WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$ARTIFACT_NAME .

EXPOSE 29092
LABEL org.opencontainers.image.source=https://github.com/jakejack13/speed
LABEL org.opencontainers.image.description="workers image"
CMD exec java -jar ${ARTIFACT_NAME}
