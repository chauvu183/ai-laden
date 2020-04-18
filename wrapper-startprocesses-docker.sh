#!/bin/bash

# see https://docs.docker.com/config/containers/multi-service_container/

if [ "$1" == "default" ]
then
    echo "usage: ./`basename "$0"` <cloud sql instance name>"
    exit 1
fi

# Start the cloud sql proxy
echo "Starting cloud_sql_proxy with instance $1"
./cloud_sql_proxy -instances=$1=tcp:3306 -credential_file=./token.json &
status=$?
if [ $status -ne 0 ]; then
  echo "Failed to start cloud_sql_proxy: $status"
  exit $status
fi

# Start the application with production-profile
java -Dspring.profiles.active=production -Djava.security.egd=file:/dev/./urandom -jar app.jar
