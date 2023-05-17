# [news.alexn.org](https://news.alexn.org)

[![.github/workflows/deploy.yml](https://github.com/alexandru/news/actions/workflows/deploy.yml/badge.svg)](https://github.com/alexandru/news/actions/workflows/deploy.yml)

Auto-generated RSS/Atom feeds.

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

This setup dumps the output into the `/var/www/news.alexn.org` directory, files which then get served by my local Nginx server. 

The only gotcha is that `$WWW_UID` and `$WWW_GID` have to be set, as these will be used for the written files, in order to avoid permission issues (files have to be readable by Nginx).
