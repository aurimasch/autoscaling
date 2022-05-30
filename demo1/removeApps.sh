#! /bin/sh
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

( cd src/main/kube &&  kubectl delete -f deployment.yml  -f service.yml -f service-monitor.yml)
wait
echo -e "${GREEN}Demo1 removed${NC}"
