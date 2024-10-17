package br.edu.ufersa.pd.weatherdatacollect.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import br.edu.ufersa.pd.weatherdatacollect.database.PseudoDatabase;
import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;

public class ReceiverThread implements Runnable {

    private MulticastSocket ms = null;
    private PseudoDatabase db = null;
    
    public ReceiverThread(MulticastSocket ms) {
        this.ms = ms;
        db = PseudoDatabase.getInstance();
    }


    @Override
    public void run() {
        boolean isRunning = true;
        byte receiverBuffer[] = new byte[1024];
        DatagramPacket receiverPacket;

        try {
            while (isRunning) {
                
                receiverPacket = new DatagramPacket(receiverBuffer, receiverBuffer.length);
                ms.receive(receiverPacket);

                String message = new String(receiverBuffer, StandardCharsets.UTF_8);
                String fields[] = message.split("#");
                

                WeatherData data = WeatherData.fromString(fields[0]);
                System.out.println(data.toString());
                db.insert(data);


            }
        } catch (SocketException e) {
            System.err.println("connection has been closed");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (!ms.isClosed()) { ms.close(); }
        }        
    }

}
