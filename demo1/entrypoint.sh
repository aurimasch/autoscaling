#!/bin/sh
java $JAVA_OPTS -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar /app.jar