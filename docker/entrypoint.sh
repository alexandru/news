#!/bin/bash

SLEEP_DURATION=1800 # 30 minutes

if [ ! -d "/opt/app/output/" ]; then
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Creating output directory ..."
    mkdir -p "/opt/app/output/"
fi

while :
do
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Generating releases.xml ..."
    # Kills process after 5 minutes
    timeout -s KILL 300 java -jar "/opt/app/gen-releases.jar" >"/tmp/releases.xml"

    if [ $? -eq 0 ]; then
        mv /tmp/releases.xml /opt/app/output/releases.xml
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Done generating releases.xml!"
    else 
        echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] ERROR generating releases.xml!" >&2
    fi

    # Sleeps for 30 minutes
    echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Sleeps for $(date "-d@$SLEEP_DURATION" -u +%H:%M:%S)"
	sleep 1800
done
