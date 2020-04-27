package ru.vtb.home.soap.client;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.addressing.server.AnnotationActionEndpointMapping;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpUrlConnection;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import ru.vtb.home.soap.servlets.FaultServlet;
import ru.vtb.home.soap.servlets.ResponseServlet;

import java.io.IOException;

@Configuration
public class SoapClientConfig {

    @Bean
    public ServletRegistrationBean responseServlet(){
        ResponseServlet servlet = new ResponseServlet();
        return new ServletRegistrationBean(servlet, "/response");
    }

    @Bean
    public ServletRegistrationBean faultServlet(){
        FaultServlet servlet = new FaultServlet();
        return new ServletRegistrationBean(servlet, "/fault");
    }

    @Bean
    public AnnotationActionEndpointMapping annotationActionEndpointMapping(){
        AnnotationActionEndpointMapping mapping = new AnnotationActionEndpointMapping();
        mapping.setMessageSender(new HttpComponentsMessageSender());
        return mapping;
    }

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext appContext){
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(appContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("host.minishift.beer");
        return marshaller;
    }

    @Bean
    public BeerClient beerClient(Jaxb2Marshaller marshaller) {
        BeerClient client = new BeerClient();
        client.setDefaultUri("http://192.168.130.11:30955/ws/beers");
//        client.setDefaultUri("http://localhost:8080/ws/beers");
//        client.setDefaultUri("http://192.168.42.9:31380/ws/beers");
//        client.setDefaultUri("http://ws-async-app.example.com:31380/ws/beers");
//        client.setDefaultUri("http://ws-async-app-paradigmus.192.168.42.9.nip.io/ws/beers");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);

        // register the LogHttpHeaderClientInterceptor
        ClientInterceptor[] interceptors =
                new ClientInterceptor[]{new LogHttpHeaderClientInterceptor()};
        client.setInterceptors(interceptors);

        return client;
    }

    private class LogHttpHeaderClientInterceptor implements ClientInterceptor {
        @Override
        public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
            try {
                TransportContext context = TransportContextHolder.getTransportContext();
                HttpUrlConnection connection =(HttpUrlConnection) context.getConnection();
                connection.addRequestHeader("test-header", "ws-async-app.example.com");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.printf("Client Request Message %s \n", messageContext.getRequest());
            return true;
        }

        @Override
        public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
            return true;
        }

        @Override
        public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
            return true;
        }

        @Override
        public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {
            // No-op
        }
    }
}
