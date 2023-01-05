NAME := ghcr.io/alexandru/news
TAG  := ${NAME}:latest

build-docker:
	docker build -f ./docker/Dockerfile -t "${TAG}" .

push-docker:
	docker push "${TAG}"
