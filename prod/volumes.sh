#! /bin/bash

kubectl apply -f pv-volume.yaml -f pv-volume2.yaml

wait

kubectl apply -f pv-volume-claim.yaml -f pv-volume-claim2.yaml

wait
echo "volumes created"