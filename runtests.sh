#!/bin/sh


( nohup kubectl port-forward svc/demo1-service -n default 30036:8081 > file 2>&1 &)
echo "Port forwarding started" 
(cd /opt/jmeter/bin && nohup sh jmeter -n -t volatile.jmx -l /home/phd && echo "Volatile") && (cd /opt/jmeter/bin && nohup sh jmeter -n -t peak.jmx -l /home/phd && echo "Peak") && (cd /opt/jmeter/bin && nohup sh jmeter -n -t ModerateIncrease.jmx -l /home/phd && echo "Modearte")&&
echo "tests ended"

#(nohup echo "1" && sleep 10 && echo "slept 10") && (nohup echo "2" && sleep 10 && echo "Slept 20")
