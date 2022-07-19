#! /bin/bash

sudo mvn clean install -P docker

wait
echo "docker file build"

sudo docker push olesiapoz/autoscaler:latest
wait 

( cd src/main/kube &&  kubectl apply -f deployment.yml )
wait
echo "Deployed"
