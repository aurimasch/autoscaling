#! /bin/bash

sudo mvn clean install -P docker

wait
echo "docker file build"

sudo docker push olesiapoz/autoscaler:latest
wait 
echo "Autoscaler pushed"
sleep 3
( cd src/main/kube &&  kubectl apply -f deployment.yml -f service.yml )
wait
echo "Autoscaler Deployed"
