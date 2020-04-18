#!/bin/bash

echo "Waiting for application startup (project: $1, instance: $2)..."
counter=0
MAXTRIES=36

while [ $counter -le $MAXTRIES ]
do
    if [[ $(gcloud compute instances get-serial-port-output $2 --project $1 --zone europe-west3-c 2> /dev/null | grep "Started Application in") ]]; then
        echo
        echo "Application has started successfully!"
        exit 0
    fi
    printf '.'
    sleep 5

    ((counter++))
done

echo
echo "WARNING: Application not available after several tries! Aborting."
echo "Check log output by using: gcloud compute instances get-serial-port-output $2 --project $1 --zone europe-west3-c"
exit 1