#!/bin/bash

if [ ! -f "/tmp/run/gen-releases.success" ]; then
    echo "WARN: last execution was not successful!" >&2
    exit 1
fi
