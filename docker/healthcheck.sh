#!/bin/bash

if [ -f "/tmp/run/gen-releases.success" ]; then
    exit 0
else
    exit 1
fi
