#! /bin/bash
helm delete prometheus
wait
echo "Prometheus is deleted"


(cd ../prod && ./cleanupVolumes.sh)

