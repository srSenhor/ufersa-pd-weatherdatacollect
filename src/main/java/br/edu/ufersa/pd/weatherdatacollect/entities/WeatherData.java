package br.edu.ufersa.pd.weatherdatacollect.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.edu.ufersa.pd.weatherdatacollect.utils.Region;

public class WeatherData {

    private float atmosphericPressure;
    private int solarRadiation;
    private float temperature;
    private float humidity;
    private Region region;
    private LocalDateTime date;
    private static DateTimeFormatter fmt;
    
    public WeatherData(float atmosphericPressure, int solarRadiation, float temperature, float humidity, Region region, LocalDateTime date) {

        setAtmosphericPressure(atmosphericPressure);
        setSolarRadiation(solarRadiation);
        setTemperature(temperature);
        setHumidity(humidity);
        setRegion(region);
        setDate(date);
        fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    }

    public float getAtmosphericPressure() {
        return atmosphericPressure;
    }
    public void setAtmosphericPressure(float atmosphericPressure) {
        this.atmosphericPressure = atmosphericPressure;
    }

    public int getSolarRadiation() {
        return solarRadiation;
    }

    public void setSolarRadiation(int solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    public float getTemperature() {
        return temperature;
    }
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }
    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }    

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String toSendFormat() {
        return atmosphericPressure + "/" + solarRadiation + "/" + temperature + "/" + humidity + "/" + region + "/" + date + "#";
    }

    public static WeatherData fromString(String input) {
        String fields[] = input.split("/");

        float atmosphericPressure = Float.parseFloat(fields[0]);
        int solarRadiation = Integer.parseInt(fields[1]);
        float temperature = Float.parseFloat(fields[2]);
        float humidity = Float.parseFloat(fields[3]);
        Region region = Region.valueOf(fields[4]);
        LocalDateTime date = LocalDateTime.parse(fields[5]);

        return new WeatherData(atmosphericPressure, solarRadiation, temperature, humidity, region, date);
    }

    @Override
    public String toString() {
        return "Region: " + region + " -> [ Pressure: " + atmosphericPressure + " hPa | Radiation: " + solarRadiation
                + " J/m² | Temperature: " + temperature + " ºC | Humidity: " + humidity + "% ]\n"
                + "|> Date: " + fmt.format(date) + "\n\n";
    }

}
