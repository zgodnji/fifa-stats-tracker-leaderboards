#!/usr/bin/env bash

mvn clean package

docker build -t ancina/leaderboards .

docker run -d -p 8084:8084 -t ancina/leaderboards