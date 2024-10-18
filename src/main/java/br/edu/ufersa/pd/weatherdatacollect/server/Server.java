package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.edu.ufersa.pd.weatherdatacollect.database.PseudoDatabase;
import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;
import br.edu.ufersa.pd.weatherdatacollect.utils.Ports;

public class Server {

    private final String GROUP_IP;
    private final boolean SHOULD_SEND;

    private ScheduledExecutorService executor;
    private PseudoDatabase db;
    private String NET_INTERFACE;

    public Server(String ip, boolean shouldSend) {
        this.db = PseudoDatabase.getInstance();
        this.GROUP_IP = ip;
        this.SHOULD_SEND = shouldSend;
        this.init();
    }

    public Server(String ip) {
        this.db = PseudoDatabase.getInstance();
        this.GROUP_IP = ip;
        this.SHOULD_SEND = false;
        this.init();
    }
    private void init() {
        
        MulticastSocket ms = null;
        Scanner cin = null;
        
        try {

            cin = new Scanner(System.in);

            ms = new MulticastSocket(Ports.MULTICAST_PORT.getValue());
            System.out.println("Server -- " + InetAddress.getLocalHost() 
                                            + " running on port "
                                            + ms.getLocalPort());

            // setting group
            
            InetAddress multicastIp = InetAddress.getByName(GROUP_IP);
            System.out.println(multicastIp.toString().substring(1));
            
            InetSocketAddress group = new InetSocketAddress(multicastIp, Ports.GROUP_PORT.getValue());
            NET_INTERFACE = NetworkInterface.getNetworkInterfaces().nextElement().getDisplayName();
            NetworkInterface netInterface = NetworkInterface.getByName(NET_INTERFACE);

            ms.joinGroup(group, netInterface);
            
            System.out.println("Receiver " + InetAddress.getLocalHost() + " has joined group addressed by " + group);

            // running threads

            switch (GROUP_IP) {
                case "225.7.8.9":
                    this.executor = Executors.newScheduledThreadPool(2);
                    executor.scheduleWithFixedDelay(new SenderThread("225.7.8.11"), 0, 1, TimeUnit.SECONDS);
                    executor.scheduleWithFixedDelay(new ReceiverThread(ms), 0, 1, TimeUnit.SECONDS);
                    executor.schedule(() -> {
                        executor.shutdownNow();
            
                        System.out.println("end execution...");
            
                        ConcurrentHashMap<Long, WeatherData> data = db.get();
            
                        System.out.println("===== Log de execucao =====");
                        data.values().stream().forEach(System.out::print);
                        System.out.println("===========================");
            
                    }, 3, TimeUnit.MINUTES);
                    break;

                case "225.7.8.11":
                    this.executor = Executors.newScheduledThreadPool(2);
                    executor.scheduleWithFixedDelay(new ReceiverThread(ms), 0, 1, TimeUnit.SECONDS);
                    executor.scheduleWithFixedDelay(new EmitterThread(SHOULD_SEND), 180, 1, TimeUnit.SECONDS);
                    executor.schedule(() -> {
                        executor.shutdownNow();

                        System.out.println("end execution...");

                        ConcurrentHashMap<Long, WeatherData> data = db.get();

                        System.out.println("===== Log de execucao =====");
                        data.values().stream().forEach(System.out::print);
                        System.out.println("===========================");

                    }, 3, TimeUnit.MINUTES);
                    break;
                default:
                    break;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cin.close();
        }
    }

}