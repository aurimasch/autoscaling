#! /bin/bash

( cd src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f service-monitor.yml)
wait
echo "Autoscaler removed"
