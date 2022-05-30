#!/bin/sh
sudo cp phd-fast.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-fast.jmx -l . )
wait
echo "Fast"
