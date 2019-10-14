#!/usr/bin/env bash

posix_read() {
    # `read -s` is not POSIX compliant, so we use `stty -echo` instead.
    stty -echo
    trap 'stty echo' EXIT
    printf "$1"
    eval "read $2"
    stty echo
    trap - EXIT
    printf "\n"

}

printf "Enter your RHN credentials and base directory into which the repository was cloned (these will be used later):\n"
read -p "Username: " username
posix_read "Password: " password
read -p "Email: " email


oc new-project credit-dispute-example

echo '
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: default-view
  namespace: credit-dispute-example
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: edit
subjects:
- kind: ServiceAccount
  name: default
  namespace: credit-dispute-example
' | oc apply -f -


# --- AMQ Streams Cluster and Kafka Topics
oc apply -f kafka/install/cluster-operator/deployment-srtimzi-cluster-operator.yaml -n credit-dispute-example
oc apply -f kafka/install/cluster/kafka-ephemeral.yaml -n credit-dispute-example
oc get pods -w -n credit-dispute-example &
cpid=$!
trap "kill $cpid" EXIT

until [ "$(oc get pods --selector strimzi.io/name=demo-cluster-kafka -o jsonpath="{.items[:3].status.containerStatuses[?(@.name == \"kafka\")].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done
until [ "$(oc get pods --selector strimzi.io/name=demo-cluster-zookeeper -o jsonpath="{.items[:3].status.containerStatuses[?(@.name == \"zookeeper\")].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done
until [ "$(oc get pods --selector strimzi.io/name=demo-cluster-entity-operator -o jsonpath="{.items[0].status.containerStatuses[:3].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done

kill $cpid
trap - EXIT

shopt -s nullglob
for topic in kafka/install/topics/*.y{a,}ml; do
    oc apply -f $topic
done
shopt -u nullglob

oc exec -it demo-cluster-kafka-0 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it demo-cluster-kafka-1 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it demo-cluster-kafka-2 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list

# --- Install Fuse on the OpenShift Cluster
oc project openshift

oc create secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=$username \
  --docker-password=\'"$password"\' \
  --docker-email=$email

BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001
oc create -n openshift -f ${BASEURL}/fis-image-streams.json

for template in eap-camel-amq-template.json \
    eap-camel-cdi-template.json \
    eap-camel-cxf-jaxrs-template.json \
    eap-camel-cxf-jaxws-template.json \
    eap-camel-jpa-template.json \
    karaf-camel-amq-template.json \
    karaf-camel-log-template.json \
    karaf-camel-rest-sql-template.json \
    karaf-cxf-rest-template.json \
    spring-boot-camel-amq-template.json \
    spring-boot-camel-config-template.json \
    spring-boot-camel-drools-template.json \
    spring-boot-camel-infinispan-template.json \
    spring-boot-camel-rest-sql-template.json \
    spring-boot-camel-teiid-template.json \
    spring-boot-camel-template.json \
    spring-boot-camel-xa-template.json \
    spring-boot-camel-xml-template.json \
    spring-boot-cxf-jaxrs-template.json \
    spring-boot-cxf-jaxws-template.json ;
do
    oc create -n openshift -f ${BASEURL}/quickstarts/${template}
done

oc create -n openshift -f ${BASEURL}/fis-console-cluster-template.json
oc create -n openshift -f ${BASEURL}/fis-console-namespace-template.json
oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml

oc get template -n openshift

# --- Create an instance of the Confluent Kafka REST Proxy for the Kafka Topics UI
oc project credit-dispute-example
oc new-app \
    -e KAFKA_REST_BOOTSTRAP_SERVERS=demo-cluster-kafka-bootstrap:9092 \
    -e KAFKA_REST_HOST_NAME=cp-kafka-rest \
    -e KAFKA_REST_LISTENERS="http://0.0.0.0:8082" \
    --name=cp-kafka-rest \
    confluentinc/cp-kafka-rest
oc expose svc/cp-kafka-rest

# --- Create an instance of the Kafka Topics UI
oc project credit-dispute-example
oc new-app \
    -e KAFKA_REST_PROXY_URL=http://cp-kafka-rest:8082 \
    -e PROXY=true \
    --name=kafka-ui \
    landoop/kafka-topics-ui
oc expose svc/kafka-ui

cd ..
git clone https://github.com/jboss-container-images/rhpam-7-openshift-image.git

cd rhpam-7-openshift-image
git checkout 7.2.x
git pull

#oc import-image -n openshift registry.access.redhat.com/rhpam-7/rhpam74-businesscentral-openshift --confirm
#oc import-image -n openshift registry.access.redhat.com/rhpam-7/rhpam74-kieserver-openshift --confirm

#oc import-image -n openshift registry.redhat.io/rhpam-7/rhpam72-businesscentral-openshift --confirm
#oc import-image -n openshift registry.redhat.io/rhpam-7/rhpam72-kieserver-openshift --confirm

oc project openshift

oc create -f rhpam72-image-streams.yaml

oc get imagestreamtag -n openshift | grep rhpam72-businesscentral-openshift
oc get imagestreamtag -n openshift | grep rhpam72-kieserver-openshift

oc project credit-dispute-example

oc new-app -f templates/rhpam72-trial-ephemeral.yaml

cd jaetest

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
    rtp-creditor-payment-service \
    rtp-creditor-receive-payment \
    rtp-creditor-fraud-detection \
    rtp-debtor-auditing \
    rtp-debtor-complete-payment \
    rtp-debtor-core-banking \
    rtp-debtor-customer-notification \
    rtp-debtor-payment-confirmation \
    rtp-debtor-payment-service \
    rtp-debtor-send-payment \
    rtp-mock
do
    printf "Deploying $service\n"
    cd $service
    mvn clean fabric8:deploy
    cd ..
done
