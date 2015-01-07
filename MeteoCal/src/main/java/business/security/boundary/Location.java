/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

/**
 *
 * @author matteomontalcini
 */
public class Location {
    
    private String latitude;
    private String longitude;
    private String country;
    private String city;
    private String description;
    private String main;
    private String icon;
    private int id;
    private int humidity;
    private int pressure;
    private float tempMax;
    private float tempMin;
    private float temp;
    private float windSpeed;
    private float windDeg;
    private int cloudPerc;

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the main
     */
    public String getMain() {
        return main;
    }

    /**
     * @param main the main to set
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the humidity
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * @param humidity the humidity to set
     */
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    /**
     * @return the pressure
     */
    public int getPressure() {
        return pressure;
    }

    /**
     * @param pressure the pressure to set
     */
    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    /**
     * @return the tempMax
     */
    public float getTempMax() {
        return tempMax;
    }

    /**
     * @param tempMax the tempMax to set
     */
    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    /**
     * @return the tempMin
     */
    public float getTempMin() {
        return tempMin;
    }

    /**
     * @param tempMin the tempMin to set
     */
    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    /**
     * @return the temp
     */
    public float getTemp() {
        return temp;
    }

    /**
     * @param temp the temp to set
     */
    public void setTemp(float temp) {
        this.temp = temp;
    }

    /**
     * @return the windSpeed
     */
    public float getWindSpeed() {
        return windSpeed;
    }

    /**
     * @param windSpeed the windSpeed to set
     */
    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * @return the windDeg
     */
    public float getWindDeg() {
        return windDeg;
    }

    /**
     * @param windDeg the windDeg to set
     */
    public void setWindDeg(float windDeg) {
        this.windDeg = windDeg;
    }

    /**
     * @return the cloudPerc
     */
    public int getCloudPerc() {
        return cloudPerc;
    }

    /**
     * @param cloudPerc the cloudPerc to set
     */
    public void setCloudPerc(int cloudPerc) {
        this.cloudPerc = cloudPerc;
    }
    
}
