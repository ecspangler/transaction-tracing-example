#!/usr/bin/env bash

oc project credit-dispute-example

# --- Deploy the Services
mvn --non-recursive clean install
for dependency in \
    credit-dispute-domain-model
do
    printf "Building $dependency\n"
    cd $dependency
    mvn clean install
    cd ..
done

for service in \
    credit-dispute-create-service \
    credit-dispute-update-service \
    credit-dispute-reg-z-service \
    credit-dispute-data-service \
    credit-dispute-customer-notify-service \
    credit-dispute-merchant-notify-service \
    credit-dispute-service
do
    printf "Deploying $service\n"
    cd $service
    mvn clean fabric8:deploy
    cd ..
done
