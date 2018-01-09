#!/usr/bin/env bash

mvn clean package

docker build -t ancina/games .

docker run -d -p 8082:8082 -t ancina/games