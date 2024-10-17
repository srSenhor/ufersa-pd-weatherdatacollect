package br.edu.ufersa.pd.weatherdatacollect.utils;

public enum Region {
    NORTH(0), SOUTH(1);
    
    private final int value;
    private Region(int value) { this.value = value; }

    public int getValue() {return this.value; }
}
