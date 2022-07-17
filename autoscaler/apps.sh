#! /bin/bash

sudo mvn clean install -P docker

wait
echo "docker file build"

( cd src/main/kube &&  kubectl apply -f deployment.yml )
wait
echo "Deployed"
