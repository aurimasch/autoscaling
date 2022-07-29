#!/bin/sh

(cd /opt/jmeter/bin &&  sh jmeter -n -t Peak.jmx -l /home/phd)
wait
echo "Peak"
