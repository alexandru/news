# [news.alexn.org](https://news.alexn.org)

[![.github/workflows/deploy.yml](https://github.com/alexandru/news/actions/workflows/deploy.yml/badge.svg)](https://github.com/alexandru/news/actions/workflows/deploy.yml)

Auto-generated RSS/Atom feeds.

## Repository Structure

- [gen-releases.kt](./gen-releases.kt): script that merges a bunch of RSS/Atom feeds in a single feed (executable if you have [JBang](https://www.jbang.dev/) installed);
- [static/](./static/): is a folder that has static files which will simply be copied over;
- [docker/Dockerfile](./docker/Dockerfile): definition for the docker image;

## Deployment

Deployment is currently done via Docker, on my own server. Here's the config that I use:

```yaml
version: '3.8'

services:
  news:
    container_name: news
    image: 'ghcr.io/alexandru/news:latest'
    restart: unless-stopped
    healthcheck:
      test: ['CMD-SHELL', '/opt/app/healthcheck.sh']
    volumes:
      - /var/www/news.alexn.org:/opt/app/output
    user: "$WWW_UID:$WWW_GID"
    environment:
      # Execute every 30 minutes (optional value)
      - CRON_INTERVAL_SECS=1800
```

This setup dumps the output of [gen-releases.kt](./gen-releases.kt) into the `/var/www/news.alexn.org` directory, files which then get served by my local Nginx server. 

The only gotcha is that `$WWW_UID` and `$WWW_GID` have to be set, as these will be used for the written files, in order to avoid permission issues (files have to be readable by Nginx).
