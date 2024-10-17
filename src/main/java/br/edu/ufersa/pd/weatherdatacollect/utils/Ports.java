package br.edu.ufersa.pd.weatherdatacollect.utils;

public enum Ports {

    MULTICAST_PORT(50000), GROUP_PORT(50001), DS_1_PORT(50002);

    private final int value;
    private Ports(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
