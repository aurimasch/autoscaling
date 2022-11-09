kubectl autoscale deployment demo1 --cpu-percent=50 --horizontal-pod-autoscaler-cpu-initialization-period=60s --horizontal-pod-autoscaler-downscale-stabilization=60s --min=1 --max=8
 kubectl apply -f components.yaml

https://stackoverflow.com/questions/54106725/docker-kubernetes-mac-autoscaler-unable-to-find-metrics
