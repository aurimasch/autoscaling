#!/bin/sh
sudo cp phd-very-slow30.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-very-slow30.jmx -l . )
wait
echo "phd very slow"
