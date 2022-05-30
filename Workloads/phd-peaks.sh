#!/bin/sh
sudo cp phd-peaks.jmx /opt/jmeter/bin
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-peaks.jmx -l . )
wait
echo "Peaks"
