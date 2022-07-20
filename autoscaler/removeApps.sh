#! /bin/bash

( cd src/main/kube &&  kubectl delete -f deployment.yml )
wait
echo "Autoscaler removed"
