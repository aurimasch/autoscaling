#! /bin/bash
echo "Installing prometheus"
(cd Prometheus && ./prometheus.sh)
wait

echo "Installing Demo1"
( cd demo1 && ./apps.sh )
wait

echo "Installing CPA"
( cd custom-pod-autoscaler && ./cpa.sh )
wait

echo "Installing Autoscaler"
( cd autoscaler && ./apps.sh )
wait
