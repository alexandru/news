#!/bin/bash
set -e

SLEEP_DURATION=1800 # 30 minutes

function generate() {
    timeout 300 java -jar "/opt/app/gen-releases.jar" >"/tmp/output/releases.xml"
}

if [ ! -d "/opt/app/output/" ]; then
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Creating output directory ..."
    mkdir -p "/opt/app/output/"
fi

while :
do
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Generating releases.xml ..."
    
    # Cleaning up temp directory
    rm -rf /tmp/output
    mkdir -p /tmp/output
    cp -rf /opt/app/static/* /tmp/output/

    if generate; then
        chown -R "$(id -u):$(id -g)" /tmp/output/
        cp -rpf /tmp/output/* /opt/app/output/
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Done generating releases.xml!"
    else 
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] ERROR generating releases.xml!" >&2
    fi

    # Sleeps for 30 minutes
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Sleeps for $(date "-d@$SLEEP_DURATION" -u +%H:%M:%S)"
	sleep "$SLEEP_DURATION"
done
