#!/usr/bin/env bash

kubectl apply -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
kubectl apply -f firstdb-deployment.yaml,firstdb-service.yaml
kubectl wait --for=condition=ready pod -l app=zoo-kafka --timeout 300
kubectl wait --for=condition=ready pod -l app=firstdb --timeout 300
kubectl apply -f first-deployment.yaml,first-service.yaml
kubectl apply -f optimizer-deployment.yaml,optimizer-service.yaml
kubectl wait --for=condition=ready pod -l app=first --timeout 300
kubectl wait --for=condition=ready pod -l app=optimizer --timeout 300
