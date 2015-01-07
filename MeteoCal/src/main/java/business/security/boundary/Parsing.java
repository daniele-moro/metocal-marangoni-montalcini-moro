package business.security.boundary;

import java.math.BigDecimal;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

@Named
@RequestScoped
public class Parsing {
    
    JSONObject jObj;

    public String parsingWeather() throws JSONException {
        RequestManager wm = new RequestManager("http://api.openweathermap.org/data/2.5/weather?q=");
        Location loc = new Location();
       
        jObj = new JSONObject(wm.getWeatherData("Milano"));
    
        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getString("lat", coordObj));
        loc.setLongitude(getString("lon", coordObj));
        
        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setCity(getString("name", jObj));
        
        JSONArray jArr = jObj.getJSONArray("weather");

        JSONObject JSONWeather = jArr.getJSONObject(0);
        loc.setId(getInt("id", JSONWeather));
        loc.setDescription(getString("description", JSONWeather));
        loc.setMain(getString("main", JSONWeather));
        loc.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        loc.setHumidity(getInt("humidity", mainObj));
        loc.setPressure(getInt("pressure", mainObj));
        loc.setTempMax(getFloat("temp_max", mainObj));
        loc.setTempMin(getFloat("temp_min", mainObj));
        loc.setTemp(getFloat("temp", mainObj));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        loc.setWindSpeed(getFloat("speed", wObj));
        loc.setWindDeg(getFloat("deg", wObj));

        // Clouds
        JSONObject cObj = getObject("clouds", jObj);
        loc.setCloudPerc(getInt("all", cObj));
        
        System.out.println("Temperatura: " + loc.getTemp());
        System.out.println("Description: " + loc.getDescription());
        
        return "login.xhtml";
    }
    
    public String parsingLatitudeLongitude() throws JSONException {
        RequestManager wm = new RequestManager("https://maps.googleapis.com/maps/api/geocode/json?address=");
        Location loc = new Location();
        
        jObj = new JSONObject(wm.getWeatherData("Milano"));
        JSONArray jArr = jObj.getJSONArray("results");
        JSONObject JSONResults = jArr.getJSONObject(0);
        JSONObject geometry = getObject("geometry", JSONResults);
        JSONObject location = getObject("location", geometry);
        loc.setLatitude(getString("lat", location));
        loc.setLongitude(getString("lng", location));
        
        System.out.println("Latitudine: " + loc.getLatitude());
        System.out.println("Longitudine: " + loc.getLongitude());
        
        
        
        return "login.xhtml";
    }
    
    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }
    
}
