package ru.vtb.home.soap.server;

import host.minishift.beer.Beer;
import host.minishift.beer.GetBeerRequest;
import host.minishift.beer.GetBeerResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;

import java.util.concurrent.TimeUnit;

@Endpoint
public class BeerEndpoint {

    private int i = 0;

    @Action("http://minishift.host/getBeerRequest")
    public @ResponsePayload
    GetBeerResponse getBeer(@RequestPayload GetBeerRequest request) throws InterruptedException {
        i++;

        System.out.println("!!! v7 i: " + i + ", request: "  + request + "\n");

        if (request.getId() == 0){
            throw new IllegalArgumentException("id cannot be 0");
        }

        TimeUnit.SECONDS.sleep(3);

        GetBeerResponse response = new GetBeerResponse();
        Beer beer = new Beer();
        beer.setId(request.getId());
        beer.setName("La Chouffe");
        response.setBeer(beer);
        return response;
    }
}