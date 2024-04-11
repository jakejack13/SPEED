#!/usr/bin/env bash

cd ..
docker compose build
docker push ghcr.io/jakejack13/speed-workers:test
docker push ghcr.io/jakejack13/speed-leaders:test
docker push ghcr.io/jakejack13/speed-first:test
docker push ghcr.io/jakejack13/speed-optimizer:test

cd kubernetes
microk8s kubectl apply -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
microk8s kubectl apply -f firstdb-deployment.yaml,firstdb-service.yaml
microk8s kubectl wait --for=condition=ready pod -l app=zoo-kafka --timeout 300s
microk8s kubectl wait --for=condition=ready pod -l app=firstdb --timeout 300s
microk8s kubectl apply -f first-deployment.yaml,first-service.yaml
microk8s kubectl apply -f optimizer-deployment.yaml,optimizer-service.yaml
microk8s kubectl wait --for=condition=ready pod -l app=first --timeout 300s
microk8s kubectl wait --for=condition=ready pod -l app=optimizer --timeout 300s
