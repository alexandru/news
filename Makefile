NAME := ghcr.io/alexandru/news
TAG  := ${NAME}:latest

.PHONY: build push

build:
	docker build -f ./docker/Dockerfile -t "${TAG}" .

push:
	docker push "${TAG}"

dependency-updates:
	./gradlew dependencyUpdates \
		-Drevision=release \
		-DoutputFormatter=html \
		--refresh-dependencies && \
		open build/dependencyUpdates/report.html

format:
	./gradlew ktlintFormat
