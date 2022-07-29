#!/bin/sh
(nohup kubectl port-forward svc/demo1-service -n default 30036:8081) &
echo "Port forwarding started" &
#echo "starting peak"
#(nohup ./peak.sh) &&
#sleep 1200
echo "starting mi"

#(echo "starting peak" && nohup ./peak.sh && sleep 1200 && echo "starting mi" && nohup ./mi.sh && sleep 1200 && echo "starting volatile" && nohup ./volatile.sh && sleep 1200 && echo "mix" && nohup ./mix.sh )
(echo "mix" && nohup ./mix.sh )

#(cd /opt/jmeter/bin && nohup sh jmeter -n -t volatile.jmx -l /home/phd && echo "Volatile" && sleep 1200) && 
#(cd /opt/jmeter/bin &&  sh jmeter -n -t peak.jmx -l /home/phd && echo "Peak" && sleep 1200) && 
#(cd /opt/jmeter/bin &&  sh jmeter -n -t ModerateIncrease.jmx -l /home/phd && echo "Moderate") &&
echo "tests ended"
#(nohup echo "1" && sleep 10 && echo "slept 10") && (nohup echo "2" && sleep 10 && echo "Slept 20")
