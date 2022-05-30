To install and deploy custom-pod-autoscaler run this command:

1) Follow these instructions: https://github.com/jthomperoo/custom-pod-autoscaler-operator/blob/master/INSTALL.md

VERSION=v1.2.0
kubectl apply -f https://github.com/jthomperoo/custom-pod-autoscaler-operator/releases/download/${VERSION}/cluster.yaml

2) Build custom sclare image

docker build -t http-request:latest .

3) Deploy to kubernetes

kubectl apply -f .\cpa.yaml 

This autoscaler queries the configurable endpoint (our custom build autoscaling application) and as result set the return value as a number of desired replicas.





