package br.edu.ufersa.pd.weatherdatacollect.drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;
import br.edu.ufersa.pd.weatherdatacollect.utils.GUI;
import br.edu.ufersa.pd.weatherdatacollect.utils.Ports;
import br.edu.ufersa.pd.weatherdatacollect.utils.Region;

public class Drone {

    private static final long COOLDOWN_READING = 1000L;
    private final String RECEIVER_GROUP_IP;

    private ScheduledExecutorService executor;
    private Region region;

    public Drone(Region region, String receiverIp) {
        this.executor = Executors.newScheduledThreadPool(1);
        this.region = region;
        this.RECEIVER_GROUP_IP = receiverIp;
        init();
    }

    private void init() {

        Runnable collectData = () -> {
            Random r = new Random();

            float atmosphericPressure = .0f;
            int solarRadiation = 0;
            float temperature = .0f;
            float humidity = .0f;
    
            switch (region) {
                case NORTH:
    
                    atmosphericPressure = r.nextFloat(1009.2f, 1019.9f);
                    solarRadiation = r.nextInt(13680, 15840);
                    temperature = r.nextFloat(23.0f, 40.0f);
                    humidity = r.nextFloat(50.0f, 80.f);
                    
                    break;
                case SOUTH:
                    
                    atmosphericPressure = r.nextFloat(1009.2f, 1019.9f);
                    solarRadiation = r.nextInt(13680, 15480);
                    temperature = r.nextFloat(16.0f, 33.0f);
                    humidity = r.nextFloat(40.0f, 90.f);
    
                    break;
                default:
                    throw new RuntimeException("cannot retrieve data from drones");
            }
    
            WeatherData data = new WeatherData(
                atmosphericPressure,
                solarRadiation,
                temperature,
                humidity,
                region,
                LocalDateTime.now()
            );
    
            byte senderBuffer[];
            DatagramPacket senderPacket;
            DatagramSocket ds = null;
    
            
            senderBuffer = data.toSendFormat().getBytes(StandardCharsets.UTF_8);
            
            try {
                
                ds = new DatagramSocket();
                senderPacket = new DatagramPacket(senderBuffer, senderBuffer.length, InetAddress.getByName(RECEIVER_GROUP_IP), Ports.MULTICAST_PORT.getValue());
                ds.send(senderPacket);
    
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(!ds.isClosed()) ds.close();
            }
        };

        Runnable printWait = () -> {
            try {

                String message = "sending data";
                
                GUI.clearScreen();
                System.out.println(message + ".");
                Thread.sleep(COOLDOWN_READING);
                
                GUI.clearScreen();
                System.out.println(message + "..");
                Thread.sleep(COOLDOWN_READING);
                
                GUI.clearScreen();
                System.out.println(message + "...");

            } catch (InterruptedException e) {
                System.err.println("execution has been terminated");
            }
        };
        
        Scanner cin = new Scanner(System.in);

        GUI.droneMenu();

        switch (cin.next()) {
            case "0":
                executor.scheduleWithFixedDelay(printWait, 0, 1, TimeUnit.SECONDS);
                executor.scheduleWithFixedDelay(collectData, 0, 3, TimeUnit.SECONDS);
                executor.schedule(() -> {
                    executor.shutdownNow();
                    System.out.println("end execution...");
                }, 70, TimeUnit.SECONDS);
                break;
            case "1":
                System.out.println("Goodbye my friend! ^~^");
                break;
            default:
                System.err.println("Unrecognized option. Closing application...");
                break;
        }

        cin.close();

        
    }

    public static void main(String[] args) {
        new Drone(Region.NORTH, "225.7.8.9");
    }
}