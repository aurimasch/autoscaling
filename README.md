# Experimental autoscaling module based on prometheus data and custom pod scaling infrastructure.

## Using scripts
Use build scripts, install your JMeter to /opt/jmeter/bin
Enter your container registry name instead of ```<enter your registry name>``` label
Scripts are designed to run on Azure Kubernetes Service. Enter Azure LB DNS label or IP instead of ```<enter DNS name of your AZ load balancer>``` .