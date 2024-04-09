#!/usr/bin/env bash

if [ "$#" -eq 0 ]; then
    kubectl='microk8s kubectl'
else
    kubectl='kubectl'
fi

$kubectl apply -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
$kubectl apply -f firstdb-deployment.yaml,firstdb-service.yaml
$kubectl wait --for=condition=ready pod -l app=zoo-kafka --timeout 300s
$kubectl wait --for=condition=ready pod -l app=firstdb --timeout 300s
$kubectl apply -f first-deployment.yaml,first-service.yaml
$kubectl apply -f optimizer-deployment.yaml,optimizer-service.yaml
$kubectl wait --for=condition=ready pod -l app=first --timeout 300s
$kubectl wait --for=condition=ready pod -l app=optimizer --timeout 300s
