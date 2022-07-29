#!/bin/sh

(cd /opt/jmeter/bin &&  sh jmeter -n -t Mix.jmx -l /home/phd )
echo "mix"
