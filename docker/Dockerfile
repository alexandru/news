FROM ghcr.io/alexandru/jdk-build-tools:latest AS cache
RUN mkdir -p /home/sdk/cache_home
ENV GRADLE_USER_HOME /home/sdk/cache_home
RUN mkdir -p /home/sdk/src
COPY --chown=sdk:sdk ./*.gradle.kts /home/sdk/src/
WORKDIR /home/sdk/src/
RUN gradle clean build -i --stacktrace --no-daemon

# ------------------------------------------------

FROM ghcr.io/alexandru/jdk-build-tools:latest AS build
COPY --chown=sdk:sdk --from=cache /home/sdk/cache_home /home/sdk/.gradle
COPY --chown=sdk:sdk . /home/sdk/src
WORKDIR /home/sdk/src

# Building distributable JAR
RUN gradle fatJar -i --stacktrace --no-daemon

# ------------------------------------------------

FROM --platform=linux/amd64 eclipse-temurin:17-alpine as jre-build

# Create a custom Java runtime
RUN apk add binutils --no-cache
RUN $JAVA_HOME/bin/jlink \
        --add-modules java.base \
        --add-modules java.xml \
        --add-modules jdk.crypto.ec \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output /javaruntime

##################################################

FROM --platform=linux/amd64 alpine:latest

# Defining default non-root user UID, GID, and name
ARG USER_UID="1000"
ARG USER_GID="1000"
ARG USER_NAME="appuser"

ENV JAVA_HOME=/opt/java
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

RUN mkdir -p /opt/app
RUN mkdir -p /opt/app/config
RUN adduser -u "${USER_GID}" -h /opt/app -s /bin/sh -D "${USER_NAME}"
WORKDIR /opt/app

RUN apk add --no-cache bash

COPY --from=build /home/sdk/src/build/libs/news-*-standalone.jar /opt/app/news.jar
COPY ./static /opt/app/static
COPY ./docker/entrypoint.sh /opt/app/
COPY ./docker/healthcheck.sh /opt/app/

RUN chown -R "${USER_NAME}:root" /opt/app
USER ${USER_NAME}

CMD /opt/app/entrypoint.sh
