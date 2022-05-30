#! /bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

kubectl delete -f ./cpa.yaml
wait
echo -e "${GREEN}CPA removed${NC}"

VERSION=v1.2.0
kubectl delete deployment custom-pod-autoscaler-operator
wait
echo -e "${GREEN}CPA operator removed${NC}"


