#!/usr/bin/env bash

cd ..
docker compose build
docker push localhost:32000/speed-workers:registry
docker push localhost:32000/speed-leaders:registry
docker push localhost:32000/speed-first:registry
docker push localhost:32000/speed-optimizer:registry

cd kubernetes
microk8s kubectl apply -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
microk8s kubectl apply -f firstdb-deployment.yaml,firstdb-service.yaml
microk8s kubectl wait --for=condition=ready pod -l app=zoo-kafka --timeout 300s
microk8s kubectl wait --for=condition=ready pod -l app=firstdb --timeout 300s
microk8s kubectl apply -f first-deployment.yaml,first-service.yaml
microk8s kubectl apply -f optimizer-deployment.yaml,optimizer-service.yaml
microk8s kubectl wait --for=condition=ready pod -l app=first --timeout 300s
microk8s kubectl wait --for=condition=ready pod -l app=optimizer --timeout 300s
