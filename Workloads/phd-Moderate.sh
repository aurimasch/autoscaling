#!/bin/sh
sudo cp phd-slow.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-moderate.jmx -l .)
wait
echo "Moderate"
