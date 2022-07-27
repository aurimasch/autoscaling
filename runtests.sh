#!/bin/sh
(nohup kubectl port-forward svc/demo1-service -n default 30036:8081) &
echo "Port forwarding started" &
(cd /opt/jmeter/bin && nohup sh jmeter -n -t volatile.jmx -l /home/phd && echo "Volatile" && sleep 600) && (cd /opt/jmeter/bin && nohup sh jmeter -n -t peak.jmx -l /home/phd && echo "Peak" && sleep 600) && (cd /opt/jmeter/bin && nohup sh jmeter -n -t ModerateIncrease.jmx -l /home/phd && echo "Modearte")&&
echo "tests ended"

#(nohup echo "1" && sleep 10 && echo "slept 10") && (nohup echo "2" && sleep 10 && echo "Slept 20")
