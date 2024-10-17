package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.net.DatagramSocket;
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

    private final int NUMBER_OF_THREADS = 1;
    private final String GROUP_IP;

    private ScheduledExecutorService executor;
    private PseudoDatabase db;
    private String NET_INTERFACE;

    public Server(String ip) {
        this.executor = Executors.newScheduledThreadPool(NUMBER_OF_THREADS);
        this.db = PseudoDatabase.getInstance();
        this.GROUP_IP = ip;
        this.init();
    }

    private void init() {
        
        MulticastSocket ms = null;
        DatagramSocket ds = null;
        Scanner cin = null;

        // CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_THREADS, () -> {});

        try {

            cin = new Scanner(System.in);

            ms = new MulticastSocket(Ports.MULTICAST_PORT.getValue());
            System.out.println("Server -- " + InetAddress.getLocalHost() 
                                            + " running on port "
                                            + ms.getLocalPort());

            // ds = new DatagramSocket();

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
                    executor.scheduleWithFixedDelay(new ReceiverThread(ms), 0, 1, TimeUnit.SECONDS);
                    executor.schedule(() -> {
                        executor.shutdownNow();
            
                        System.out.println("end execution...");
            
                        ConcurrentHashMap<Long, WeatherData> data = db.get();
            
                        System.out.println("===== Log de execucao =====");
                        data.values().stream().forEach(System.out::print);
                        System.out.println("===========================");
            
                    }, 70, TimeUnit.SECONDS);

                case "225.7.8.10":
                        // executor.scheduleWithFixedDelay(printWait, 0, 1, TimeUnit.SECONDS);
                        // executor.scheduleWithFixedDelay(new TransferThread(ms, ds, "225.0.0.20", ID), 10, 1, TimeUnit.SECONDS);
                        // executor.schedule(() -> {
                        //     executor.shutdownNow();
                
                        //     System.out.println("end execution...");
                
                        //     ConcurrentHashMap<Long, WeatherData> data = db.get();
                
                        //     System.out.println("===== Log de execucao =====");
                        //     data.values().stream().forEach(System.out::print);
                        //     System.out.println("===========================");
                
                        // }, 70, TimeUnit.SECONDS);
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

    // private static void await(CyclicBarrier barrier, String debug) {
    //     try {
    //         System.out.println(debug + " bateu na barreira");
    //         barrier.await();
    //     } catch (InterruptedException | BrokenBarrierException e) {
    //         e.printStackTrace();
    //     }
    // }

    public static void main(String[] args) {
        new Server("225.7.8.9");
    }

}
