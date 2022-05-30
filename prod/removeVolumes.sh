#!/bin/sh
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color
kubectl delete -f pv-volume-claim.yaml -f pv-volume-claim2.yaml

wait

kubectl delete -f pv-volume.yaml -f pv-volume2.yaml

wait
echo -e "${GREEN}Volumes are deleted${NC}"