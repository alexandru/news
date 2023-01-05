NAME := ghcr.io/alexandru/news
TAG  := ${NAME}:latest

build:
	docker build -f ./docker/Dockerfile -t "${TAG}" .

push:
	docker push "${TAG}"
