#!/usr/bin/env bash

microk8s kubectl delete -f rbac-authorization.yaml
microk8s kubectl delete -f zoo-kafka-deployment.yaml,zoo-kafka-service.yaml
microk8s kubectl delete -f firstdb-deployment.yaml,firstdb-service.yaml
microk8s kubectl delete -f first-deployment.yaml,first-service.yaml
microk8s kubectl delete -f optimizer-deployment.yaml,optimizer-service.yaml
