package ru.vtb.home.soap.client;

import host.minishift.beer.GetBeerRequest;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.addressing.client.ActionCallback;
import org.springframework.ws.soap.addressing.core.EndpointReference;

import java.net.URI;
import java.net.URISyntaxException;

public class BeerClient extends WebServiceGatewaySupport {

    public void sendGetBeerRequest(Integer id) throws URISyntaxException {
        GetBeerRequest request = new GetBeerRequest();
        request.setId(id);

        ActionCallback callback = new ActionCallback(new URI("http://minishift.host/getBeerRequest"));
        callback.setReplyTo(new EndpointReference(
                new URI("http://minishift.host:8082/response")));
        callback.setFaultTo(new EndpointReference(
                new URI("http://minishift.host:8082/fault")));
        getWebServiceTemplate().marshalSendAndReceive(request, callback);

//        ActionCallback callback = new ActionCallback(new URI("http://minishift.host/getBeerRequest")) {
//
//            @Override
//            public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
////                TransportContext context = TransportContextHolder.getTransportContext();
////                HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
////                connection.addRequestHeader("Host", "ws-async-app.example.com");
//
////                SoapMessage soapMessage = (SoapMessage)message;
////                SoapHeader header = soapMessage.getSoapHeader();
////                header.addAttribute(new QName("Host"), "ws-async-app.example.com");
//
//                super.doWithMessage(message);
//
////                connection.addRequestHeader("Host", "ws-async-app.example.com");
//            }
//        };
//        callback.setReplyTo(new EndpointReference(new URI("http://localhost:8080/response")));
//        callback.setFaultTo(new EndpointReference(new URI("http://localhost:8080/fault")));
//        getWebServiceTemplate().marshalSendAndReceive(request, callback);

    }

}