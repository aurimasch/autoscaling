#!/bin/sh

(cd /opt/jmeter/bin && nohup sh jmeter -n -t volatile.jmx -l /home/phd)
wait
echo "Volatile
