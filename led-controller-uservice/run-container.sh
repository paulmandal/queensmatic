#!/bin/bash

SVC_NAME=led-controller-uservice
docker stop $SVC_NAME
docker build . -t $SVC_NAME
docker run -p 0.0.0.0:31337:31337 --device=/dev/ttyACM0 $SVC_NAME
