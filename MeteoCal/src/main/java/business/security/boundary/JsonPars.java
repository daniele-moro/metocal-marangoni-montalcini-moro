package business.security.boundary;

import business.security.entity.WeatherCondition;
import business.security.object.Location;
import javax.ejb.Singleton;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

@Singleton
public class JsonPars {
    
    JSONObject jObj;

    public WeatherCondition parsingWeather(String lat, String lng) throws JSONException {
        RequestManager wm = new RequestManager("http://api.openweathermap.org/data/2.5/weather?lat=", lat, lng);
        WeatherCondition wc = new WeatherCondition();
        String mainResult;
        
        jObj = new JSONObject(wm.getData());
        JSONArray jArr = jObj.getJSONArray("weather");
        JSONObject JSONWeather = jArr.getJSONObject(0);
        mainResult = getString("main", JSONWeather);
        switch(mainResult) {
            case "Thunderstorm" : 
                wc.setPrecipitation(true);
                break; 
            case "Drizzle" : 
                wc.setPrecipitation(true);
                break; 
            case "Rain" :
                wc.setPrecipitation(true);
                break; 
            case "Snow" : 
                wc.setPrecipitation(true);
                break; 
            case "Atmpsphere" : 
                wc.setPrecipitation(false);
                break; 
            case "Clouds" : 
                wc.setPrecipitation(false);
                break; 
            case "Extreme" : 
                wc.setPrecipitation(false);
                break; 
            default : 
                wc.setPrecipitation(false);
                break; 
        }
        
        //Icon
        wc.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        
        //Temperature
        wc.setTemperature(getFloat("temp", mainObj));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        wc.setWind(getFloat("speed", wObj));
        
        return wc;
    }
    
    public Location parsingLatitudeLongitude(String city) throws JSONException {
        //Change backspace with %20 for the request
        String replace = city.replace(" ", "%20");
        RequestManager wm = new RequestManager("https://maps.googleapis.com/maps/api/geocode/json?address=", replace);
        Location loc = new Location();
        
        jObj = new JSONObject(wm.getData());
        JSONArray jArr = jObj.getJSONArray("results");
        JSONObject JSONResults = jArr.getJSONObject(0);
        JSONObject geometry = getObject("geometry", JSONResults);
        JSONObject location = getObject("location", geometry);
        loc.setLatitude(getString("lat", location));
        loc.setLongitude(getString("lng", location));
        
        System.out.println("Latitudine: " + loc.getLatitude());
        System.out.println("Longitudine: " + loc.getLongitude());
        
        return loc;
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
