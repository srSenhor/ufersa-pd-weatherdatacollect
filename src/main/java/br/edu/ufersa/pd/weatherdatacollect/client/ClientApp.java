package br.edu.ufersa.pd.weatherdatacollect.client;

public class ClientApp {
    public static void main(String[] args) {

        String [] wantReceive = {"pressure"};
        // String [] wantReceive = {"temperature", "humidity"};
        // String [] wantReceive = {"pressure", "radiation", "temperature", "humidity"};

        new Client(wantReceive);
    }
}
