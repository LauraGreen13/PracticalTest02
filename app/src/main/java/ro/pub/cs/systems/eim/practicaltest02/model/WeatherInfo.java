package ro.pub.cs.systems.eim.practicaltest02.model;

/**
 * Created by laura on 13.05.2016.
 */
public class WeatherInfo {
    private float temperature;
    private float windSpeed;
    private String generalState;
    private float pressure;
    private float humidity;


    public WeatherInfo() {}
    public WeatherInfo(float temperature, float windSpeed, String generalState, float pressure, float humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.generalState = generalState;
        this.pressure = pressure;
        this.humidity = humidity;
    }


    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getGeneralState() {
        return generalState;
    }

    public void setGeneralState(String generalState) {
        this.generalState = generalState;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
}
