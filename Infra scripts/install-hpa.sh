#! /bin/bash
echo "Installing prometheus"
(cd ../Prometheus && ./prometheus.sh)
wait
sleep 40
echo "Installing Demo1"
( cd ../demo1 && ./apps.sh )
wait
sleep 30
echo "Installing Autoscaler"
( cd ../autoscaler && ./apps.sh )
wait
sleep 30
echo "Installing Autoscaler"
( cd ../prod/HPAs && kubectl apply -f hpa.yml )

#wait
#sleep 240
#nohup ./runtests.sh


