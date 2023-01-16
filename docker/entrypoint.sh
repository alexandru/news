#!/bin/bash
set -e

if [ -z "$CRON_INTERVAL_SECS" ]; then
    CRON_INTERVAL_SECS=1800 # defaults to 30 minutes
fi

if [ -z "$COMMAND_TIMEOUT_SECS" ]; then
    COMMAND_TIMEOUT_SECS=300 # defaults to 5 minutes
fi

function generate() {
    timeout -s KILL "$COMMAND_TIMEOUT_SECS" java -jar "/opt/app/gen-releases.jar" >"/tmp/output/releases.xml"
}

if [ -f "/tmp/run/gen-releases.pid" ]; then 
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] gen-releases is already running!" >&2
    exit 1
fi

mkdir -p /tmp/run
echo "$$" > /tmp/run/gen-releases.pid
trap "rm -f /tmp/run/gen-releases.pid" EXIT

if [ ! -d "/opt/app/output/" ]; then
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Creating output directory ..."
    mkdir -p "/opt/app/output/"
fi

while :; do
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Generating releases.xml ..."

    # Cleaning up temp directory
    rm -rf /tmp/output
    mkdir -p /tmp/output
    cp -rf /opt/app/static/* /tmp/output/

    if generate; then
        chown -R "$(id -u):$(id -g)" /tmp/output/
        cp -rpf /tmp/output/* /opt/app/output/
        touch /tmp/run/gen-releases.success
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Done generating releases.xml!"
    else
        rm -f /tmp/run/gen-releases.success
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] ERROR generating releases.xml!" >&2
    fi

    # Sleeps for 30 minutes
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Sleeps for $(date "-d@$CRON_INTERVAL_SECS" -u +%H:%M:%S)"
    sleep "$CRON_INTERVAL_SECS"
done
