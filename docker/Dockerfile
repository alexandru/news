FROM ghcr.io/alexandru/jdk-build-tools:latest AS build
COPY --chown=sdk:sdk . /home/sdk/src
WORKDIR /home/sdk/src

# Building distributable JAR
RUN rm -rf ./build && mkdir ./build
RUN chmod a+x "$HOME/.sdkman/bin/sdkman-init.sh"
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"
RUN jbang export --verbose portable --output ./build/gen-releases.jar ./gen-releases.kt

##################################################

FROM --platform=linux/amd64 alpine:latest

# Defining default non-root user UID, GID, and name
ARG USER_UID="1000"
ARG USER_GID="1000"
ARG USER_NAME="appuser"

RUN mkdir -p /opt/app
RUN mkdir -p /opt/app/config
RUN adduser -u "${USER_GID}" -h /opt/app -s /bin/sh -D "${USER_NAME}"
WORKDIR /opt/app
RUN chown -R "${USER_NAME}" /opt/app && chmod -R "g+rwX" /opt/app && chown -R ${USER_NAME}:root /opt/app

RUN apk add --no-cache openjdk17-jre-headless
# RUN apk add --no-cache cron

COPY --from=build --chown=${USER_NAME}:root /home/sdk/src/build/lib /opt/app/lib
COPY --from=build --chown=${USER_NAME}:root /home/sdk/src/build/gen-releases.jar /opt/app/
COPY --chown=${USER_NAME}:root ./docker/gen-releases-task.sh /opt/app/

# Installing crontab
RUN rm -f /etc/crontabs/root
COPY --chown=${USER_NAME}:root ./docker/crontab /etc/crontabs/${USER_NAME}

USER ${USER_NAME}
CMD crond -f -S -l 0
