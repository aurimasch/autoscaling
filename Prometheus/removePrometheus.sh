#! /bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

helm delete prometheus
wait
echo -e "${GREEN}Prometheus is deleted${NC}"


(cd ../prod && ./removeVolumes.sh)