#!/bin/sh

(cd /opt/jmeter/bin && nohup sh jmeter -n -t ModerateIncrease.jmx -l /home/phd)
wait
echo "moderate"
