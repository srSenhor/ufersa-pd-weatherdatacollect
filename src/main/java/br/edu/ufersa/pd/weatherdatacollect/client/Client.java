package br.edu.ufersa.pd.weatherdatacollect.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.*;

public class Client {
    
    private final String EXCHANGE;
    private final List<String> ROUTINGKEYS;
    private final ConnectionFactory factory;

    public Client(String [] wantReceive) {
        this.EXCHANGE = "weatherdata";
        this.ROUTINGKEYS = Arrays.asList(wantReceive) ;
        this.factory = new ConnectionFactory();
        this.init();
    }

    private void init() {

        factory.setHost("localhost");
        Connection conn = null;
        Channel channel = null;
        

        try {

            conn = factory.newConnection();
            channel = conn.createChannel();

            channel.exchangeDeclare(EXCHANGE, "direct");
            String queueName = channel.queueDeclare().getQueue();

            // TODO: Ver um jeito de substituir por uma lambda expression, usando o forEach da stream

            for (String routingKey : ROUTINGKEYS) {
                if (ROUTINGKEYS.contains("all")) {
                    channel.queueBind(queueName, EXCHANGE, "#");
                    break;
                }

                channel.queueBind(queueName, EXCHANGE, routingKey);
            }

            System.out.println("=== User waiting for some messages... ===");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("|> Received data (" 
                                    + delivery.getEnvelope().getRoutingKey() 
                                    + ":"
                                    + message 
                                    + ")");
            };

            channel.basicConsume(queueName, true, deliverCallback, (consumerTag) -> {});

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}