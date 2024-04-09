#!/usr/bin/env bash

kubectl delete -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
kubectl delete -f firstdb-deployment.yaml,firstdb-service.yaml
kubectl delete -f first-deployment.yaml,first-service.yaml
kubectl delete -f optimizer-deployment.yaml,optimizer-service.yaml
