package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import br.edu.ufersa.pd.weatherdatacollect.database.PseudoDatabase;
import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;
import br.edu.ufersa.pd.weatherdatacollect.utils.Ports;

public class SenderThread implements Runnable {
    
    private final String RECEIVER_GROUP_IP;
    private final PseudoDatabase db;
    private static long  objId;

    public SenderThread(String receiverIp) {
        this.RECEIVER_GROUP_IP = receiverIp;
        db = PseudoDatabase.getInstance();
        objId = 0;
    }
    
    @Override
    public void run() {

        byte senderBuffer[];
        DatagramPacket senderPacket;
        DatagramSocket ds = null;

        WeatherData data = db.get(objId);
        
        if (data != null) {
         
            objId++;
            
            senderBuffer = data.toSendFormat().getBytes(StandardCharsets.UTF_8);
            System.out.println("DEBUG: Object ID " + objId);
            
            try {
                
                ds = new DatagramSocket();
                senderPacket = new DatagramPacket(senderBuffer, senderBuffer.length, InetAddress.getByName(RECEIVER_GROUP_IP), Ports.MULTICAST_PORT.getValue());
                ds.send(senderPacket);
    
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(!ds.isClosed()) ds.close();
            }
        }
        

    }
}
