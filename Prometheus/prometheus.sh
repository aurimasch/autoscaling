#! /bin/bash
echo "Creating persitant volumes"

(cd ../prod && ./volumes.sh)

wait
set -e

helm install prometheus -f values.yaml prometheus-community/prometheus

wait 


echo "patching helming"
kubectl patch ds prometheus-node-exporter --type "json" -p '[{"op": "remove", "path" : "/spec/template/spec/containers/0/volumeMounts/2/mountPropagation"}]'
wait

echo "finished"
