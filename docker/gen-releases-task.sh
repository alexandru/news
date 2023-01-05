#!/bin/sh

echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Generating releases.xml ..."
mkdir -p "/opt/app/output/"
exec java -jar "/opt/app/gen-releases.jar" >"/opt/app/output/releases.xml"
echo "[$(date +"%Y-%m-%dT%H:%M:%S%z")] Done generating releases.xml!"
