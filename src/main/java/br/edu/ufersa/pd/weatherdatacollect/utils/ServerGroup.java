package br.edu.ufersa.pd.weatherdatacollect.utils;

public enum ServerGroup {
    RECEIVER(0), DISTRIBUTOR(1);
    
    private final int value;
    private ServerGroup(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
