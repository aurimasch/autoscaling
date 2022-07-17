#! /bin/bash

VERSION=v1.2.0
kubectl apply -f https://github.com/jthomperoo/custom-pod-autoscaler-operator/releases/download/${VERSION}/cluster.yaml

wait
echo "CPA operator installed"

sudo docker build -t olesiapoz/http-request-autoscaler:latest .
wait
echo "Build custom sclare image"

kubectl apply -f ./cpa.yaml
wait
echo "Deploy to kubernetes"
