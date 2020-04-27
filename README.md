# soap-ws-addressing

# Проект содержит soap-ws сервера и клиента.
№
# Сервер отвечает клиенту асинхронно, т.е. на полученный запрос, формирует ответ и отправляет его на указанный клиентом
# адрес, согласно спецификации WS-Addressing
#
# Специально для получения асинхронных ответов сервера, клиент поднимает 2 сервлета: один для ответа, другой для ошибок
#
# Сервер запускается на порту 8080
# Клиент запускается на порту 8082
#
# Предполагается, что сервер будет запущен в Docker контейнере, например в OpenShift
#
#
#
#

#
eval $(crc oc-env)

#
docker login -u kubeadmin -p $(oc whoami -t) default-route-openshift-image-registry.apps-crc.testing

#
oc delete project soap-ws-async

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

# curl -v --data "" -HHost:minishift.host http://minishift.host:8082/response
#
#
#