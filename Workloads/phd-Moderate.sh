#!/bin/sh
sudo cp phd-moderate.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-moderate.jmx -l .)
wait
echo "Moderate"
