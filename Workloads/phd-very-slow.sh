#!/bin/sh
sudo cp phd-very-slow.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-very-slow.jmx -l . )
wait
echo "opo very slow"
