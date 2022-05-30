#!/bin/sh
sudo cp phd-peaks-with-pause.jmx /opt/jmeter/bin && 
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-peaks-with-pause.jmx -l . )
wait
echo "peaks-with-pause"
