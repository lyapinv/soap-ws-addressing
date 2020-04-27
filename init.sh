#!/bin/bash
echo "Start init..."

#
eval $(crc oc-env)

#
docker login -u kubeadmin -p $(oc whoami -t) default-route-openshift-image-registry.apps-crc.testing

#
oc delete project soap-ws-async

echo "Wait for project being deleted"
sleep 3;

echo "Create new project"
#
oc new-project soap-ws-async

#
CRC_REGISTRY=default-route-openshift-image-registry.apps-crc.testing

#
mvn clean package

#
docker build -t soap-ws-async/ws-async-app test-soap-ws-server/.

#
docker tag soap-ws-async/ws-async-app $CRC_REGISTRY/soap-ws-async/ws-async-app

#
docker push $CRC_REGISTRY/soap-ws-async/ws-async-app

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
