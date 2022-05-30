#!/bin/sh
( until  ( kubectl port-forward svc/demo1-service -n default 30036:30036 )  ; do
    echo "failed and restarting";
    sleep 1
done ) 
