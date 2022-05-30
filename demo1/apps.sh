#! /bin/bash

sudo mvn clean install -P docker
wait

sudo docker push olesiapoz/demo1:latest
wait

echo "docker file build and pushed"

( cd src/main/kube &&  kubectl apply -f deployment.yml -f service.yml -f service-monitor.yml)
wait
echo "Demo1 is deployed"
