#!/bin/sh
sudo cp phd-mix.jmx /opt/jmeter/bin
(cd /opt/jmeter/bin &&  sh jmeter -n -t phd-mix.jmx -l . )
echo "mix"
