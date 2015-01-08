package business.security.boundary;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

@Stateless
public class JsonPars {
    
    JSONObject jObj;

    public String parsingWeather(String lat, String lng) throws JSONException {
        //Dovremo usare poi lat e lng al posto dei numeri per inizializzare l'URL
        RequestManager wm = new RequestManager("http://api.openweathermap.org/data/2.5/weather?lat=", "45.44692999999999", "8.622161199999999");
        Location loc = new Location();
        
        jObj = new JSONObject(wm.getData());
        JSONArray jArr = jObj.getJSONArray("weather");
        JSONObject JSONWeather = jArr.getJSONObject(0);
        loc.setDescription(getString("description", JSONWeather));
        loc.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        loc.setTemp(getFloat("temp", mainObj));

        // Wind
        JSONObject wObj = getObject("wind", jObj);
        loc.setWindSpeed(getFloat("speed", wObj));

        // Clouds
        JSONObject cObj = getObject("clouds", jObj);
        loc.setCloudPerc(getInt("all", cObj));
        
        System.out.println(loc.getDescription());
        System.out.println(loc.getTemp());
        System.out.println(loc.getWindSpeed());
        System.out.println(loc.getCloudPerc());

        return "login.xhtml";
    }
    
    public Location parsingLatitudeLongitude(String city) throws JSONException {
        //Dovremo usare la city per inizializzare l'URL
        RequestManager wm = new RequestManager("https://maps.googleapis.com/maps/api/geocode/json?address=", city);
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
