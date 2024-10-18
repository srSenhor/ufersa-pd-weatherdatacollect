package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.edu.ufersa.pd.weatherdatacollect.database.PseudoDatabase;
import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;

public class EmitterThread implements Runnable {

    private final String EXCHANGE;
    private final List<String> ROUTINGKEYS;
    private final ConnectionFactory factory;
    private final boolean SHOULD_SEND;
    private PseudoDatabase db;

    public EmitterThread(boolean shouldSend) {
        this.EXCHANGE = "weatherdata";
        this.factory = new ConnectionFactory();
        ROUTINGKEYS = Arrays.asList("pressure", "temperature", "humidity", "radiation");
        this.SHOULD_SEND = shouldSend;
        this.db = PseudoDatabase.getInstance();
    }

    @Override
    public void run() {

        if (SHOULD_SEND) {
            
            factory.setHost("localhost");
    
            try (   Connection conn = factory.newConnection();
                    Channel channel = conn.createChannel()) {
    
                channel.exchangeDeclare(EXCHANGE, "direct");
    
                for (WeatherData data : db.get().values()) {
                    ROUTINGKEYS.stream().forEach((routingKey) -> {
                        String message =    "{ region : " + data.getRegion() + ", " 
                                        +   "date : " + data.formattedDate() + ", "
                                        +   routingKey + " : ";
    
                        switch (routingKey) {
                            case "pressure":
                                message = message + data.getAtmosphericPressure();
                                break;
                            case "radiation":
                                message = message + data.getSolarRadiation();
                                break;                        
                            case "temperature":
                                message = message + data.getTemperature();
                                break;                        
                            case "humidity":
                                message = message + data.getHumidity();
                                break;
                            default:
                                message = message + "ERROR ON DATA";
                                break;
                        }
    
                        message = message + " }";
    
                        try {
                            channel.basicPublish(EXCHANGE, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
    
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
            
        }
    }

}