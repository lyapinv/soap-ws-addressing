package ru.vtb.home.soap;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import ru.vtb.home.soap.client.BeerClient;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class RunClient {

    /* Must receive 2 responses: success and fault */
    public static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        ApplicationContext context = null;
        try {
            context = SpringApplication.run(RunClient.class);

            BeerClient client = context.getBean(BeerClient.class);
            for (int i = 0; i < 3; i++) {
                client.sendGetBeerRequest(1);
                System.out.println("getBeerRequest handled by replyTo");
                TimeUnit.SECONDS.sleep(1);
            }

            client.sendGetBeerRequest(0);
            System.out.println("getBeerRequest handled by faultTo");
        } catch (BeansException e) {
            e.printStackTrace();
        }

        latch.await(10, TimeUnit.SECONDS);
        ((ConfigurableApplicationContext) context).close();
    }

}