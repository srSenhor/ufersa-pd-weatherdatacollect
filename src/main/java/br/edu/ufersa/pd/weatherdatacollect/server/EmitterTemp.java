package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.edu.ufersa.pd.weatherdatacollect.database.PseudoDatabase;
import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;
import br.edu.ufersa.pd.weatherdatacollect.utils.Region;

public class EmitterTemp {

    private final String EXCHANGE;
    private final List<String> ROUTINGKEYS;
    private final ConnectionFactory factory;

    public EmitterTemp() {
        this.EXCHANGE = "weatherdata";
        this.factory = new ConnectionFactory();
        ROUTINGKEYS = Arrays.asList("pressure", "temperature", "humidity", "radiation");
        this.init();
    }

    private void init() {

        Random r = new Random();

        float atmosphericPressure = .0f;
        int solarRadiation = 0;
        float temperature = .0f;
        float humidity = .0f;

        PseudoDatabase db = PseudoDatabase.getInstance();

        for (int i = 0; i < 10; i++) {
            atmosphericPressure = r.nextFloat(1009.2f, 1019.9f);
            solarRadiation = r.nextInt(13680, 15840);
            temperature = r.nextFloat(23.0f, 40.0f);
            humidity = r.nextFloat(50.0f, 80.f);
            
            db.insert(new WeatherData(
                atmosphericPressure,
                solarRadiation,
                temperature,
                humidity,
                Region.NORTH,
                LocalDateTime.now()
            ));
        }

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
                        Thread.sleep(3000);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new EmitterTemp();
    }
}
