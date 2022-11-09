#! /bin/bash

sudo mvn clean install -P docker
wait

sudo docker push <enter your registry name>/demo1:latest
wait

echo "docker file build and pushed"

( cd src/main/kube &&  kubectl apply -f pod.yml -f service.yml )
wait
echo "Deployed"
