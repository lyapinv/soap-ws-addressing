#!/bin/bash
echo "Start init..."

#
eval $(crc oc-env)

#
docker login -u kubeadmin -p $(oc whoami -t) default-route-openshift-image-registry.apps-crc.testing

# Delete project and wait until all its resources being deleted - need for success creation new project
oc delete project soap-ws-async
DEL_RES="$(oc get project.project.openshift.io/soap-ws-async --show-kind --ignore-not-found)"
while [ ${#DEL_RES} != 0 ];
    do
        echo "Updating";
        sleep 1;
        DEL_RES="$(oc get project.project.openshift.io/soap-ws-async --show-kind --ignore-not-found)";
        echo "DEBUG. Available symbols" : ${#DEL_RES};
    done && echo "Finish waiting for project deletion.\n";

echo "Create new project"
#
oc new-project soap-ws-async

#
CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing

#
mvn clean package

#
docker build -t soap-ws-async/ws-async-app:v7 test-soap-ws-server/.

#
docker tag soap-ws-async/ws-async-app:v7 $CRC_REGISTRY/soap-ws-async/ws-async-app:v7

#
docker push $CRC_REGISTRY/soap-ws-async/ws-async-app:v7

#
oc delete deployment ws-async-app -n soap-ws-async

#
oc apply -f Istio-ws-async/deployment-server.yml -n soap-ws-async

#
oc apply -f Istio-ws-async/service-server.yml -n soap-ws-async

# Ingress
#
oc apply -f Istio-ws-async/ingress-gateway.yaml -n soap-ws-async
#
oc apply -f Istio-ws-async/ingress-virtualService.yaml -n soap-ws-async

# Egress
#
oc apply -f Istio-ws-async/egress-gateway.yaml -n soap-ws-async
#
oc apply -f Istio-ws-async/egress-VirtualService.yaml -n soap-ws-async
#
oc apply -f Istio-ws-async/egress-ServiceEntry.yaml -n soap-ws-async
#
oc apply -f Istio-ws-async/egress-DestinationRule.yaml -n soap-ws-async

# ----------------------------------------- Custom
# Ограничить пул коннектов для Ingress трафика
oc apply -f Istio-ws-async/ingress-DestinationRule.yaml -n soap-ws-async
# -----------------------------------------

#
#
#
#
#
echo "Finish init."

export INGRESS_HOST=$(crc ip)
export INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')

export WS_SERVER_URI="http://"$INGRESS_HOST:$INGRESS_PORT"/ws/beers"
echo "Call WS with the next URI: " $WS_SERVER_URI
