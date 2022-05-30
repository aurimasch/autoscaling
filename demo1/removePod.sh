#! /bin/sh
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

( cd src/main/kube &&  kubectl delete -f pod.yml  -f service.yml )
wait
echo -e "${GREEN}Demo1 removed${NC}"
