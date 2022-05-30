#! /bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo "Removing Autoscaler"
( cd ../autoscaler && ./removeApps.sh )
wait

echo "Removing Demo1"
( cd ../demo1 && ./removeApps.sh )
wait

echo "Removing CPA"
( cd ../custom-pod-autoscaler && ./removeCpa.sh )
wait


echo "Removing prometheus"
(cd ../Prometheus && ./removePrometheus.sh)
wait





