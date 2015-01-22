package business.security.control;

import business.security.entity.WeatherCondition;
import business.security.object.Location;
import exception.WeatherException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Singleton;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

@Singleton
public class JsonPars {

    JSONObject jObj;

    /**
     *
     * @param lat
     * @param lng
     * @param eventStart
     * @return
     * @throws WeatherException Exception if there's problem with the server or
     * the date is after 15 days
     */
    public WeatherCondition parsingWeather(String lat, String lng, Date eventStart) throws WeatherException {
        RequestManager wm = new RequestManager("http://api.openweathermap.org/data/2.5/forecast/daily?lat=", lat, lng);
        WeatherCondition wc = new WeatherCondition();
        String mainResult;
        long forecastDateUnix;
        boolean setted = false;
        Date currentDate = new Date();

        try {
            jObj = new JSONObject(wm.getData());
            JSONArray jArr = jObj.getJSONArray("list");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject JSONWeather = jArr.getJSONObject(i);

                //Forecast Date
                forecastDateUnix = getLong("dt", JSONWeather);
                Date dataEstratta = new Date((long) forecastDateUnix * 1000);
                if (dataEstratta.getDate() == eventStart.getDate() && dataEstratta.getMonth() == eventStart.getMonth() && dataEstratta.getYear() == eventStart.getYear()) {

                    setted = true;

                    //Wind Speed
                    wc.setWind(getFloat("speed", JSONWeather));

                    //Temperature
                    JSONObject tempObj = getObject("temp", JSONWeather);
                    wc.setTemperature(getFloat("day", tempObj));

                    //Main
                    JSONArray weatherArr = JSONWeather.getJSONArray("weather");
                    JSONObject weatherObj = weatherArr.getJSONObject(0);
                    mainResult = getString("main", weatherObj);
                    switch (mainResult) {
                        case "Thunderstorm":
                            wc.setPrecipitation(true);
                            break;
                        case "Drizzle":
                            wc.setPrecipitation(true);
                            break;
                        case "Rain":
                            wc.setPrecipitation(true);
                            break;
                        case "Snow":
                            wc.setPrecipitation(true);
                            break;
                        case "Atmosphere":
                            wc.setPrecipitation(false);
                            break;
                        case "Clouds":
                            wc.setPrecipitation(false);
                            break;
                        case "Extreme":
                            wc.setPrecipitation(false);
                            break;
                        default:
                            wc.setPrecipitation(false);
                            break;
                    }

                    //Icon
                    wc.setIcon(getString("icon", weatherObj));
                    System.out.println("Data estratta: " + dataEstratta);
                    System.out.println("Data richiesta: " + eventStart);
                    return wc;
                }
            }
            throw new WeatherException("No weather disponible (after 15 days)");
        } catch (Exception ex) {
            throw new WeatherException("Error in loading weather");
        }
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

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    private static long getLong(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getLong(tagName);
    }

    public List<WeatherCondition> weatherForecastNextDays(String lat, String lng, Date startDate) throws WeatherException {
        RequestManager wm = new RequestManager("http://api.openweathermap.org/data/2.5/forecast/daily?lat=", lat, lng);

        String mainResult;
        long forecastDateUnix;

        try {

            List<WeatherCondition> listWeather = new ArrayList<>();
            jObj = new JSONObject(wm.getData());
            JSONArray jArr = jObj.getJSONArray("list");
            for (int i = 0; i < jArr.length(); i++) {
                WeatherCondition wc = new WeatherCondition();
                JSONObject JSONWeather = jArr.getJSONObject(i);

                //Forecast Date
                forecastDateUnix = getLong("dt", JSONWeather);

                Date dataEstratta = new Date((long) forecastDateUnix * 1000);
                if ((dataEstratta.getDate() == startDate.getDate()
                        && dataEstratta.getMonth() == startDate.getMonth()
                        && dataEstratta.getYear() == startDate.getYear())
                        || dataEstratta.after(startDate)) {

                     //Wind Speed
                    wc.setWind(getFloat("speed", JSONWeather));

                    //Temperature
                    JSONObject tempObj = getObject("temp", JSONWeather);
                    wc.setTemperature(getFloat("day", tempObj));

                    //Main
                    JSONArray weatherArr = JSONWeather.getJSONArray("weather");
                    JSONObject weatherObj = weatherArr.getJSONObject(0);
                    mainResult = getString("main", weatherObj);
                    switch (mainResult) {
                        case "Thunderstorm":
                            wc.setPrecipitation(true);
                            break;
                        case "Drizzle":
                            wc.setPrecipitation(true);
                            break;
                        case "Rain":
                            wc.setPrecipitation(true);
                            break;
                        case "Snow":
                            wc.setPrecipitation(true);
                            break;
                        case "Atmosphere":
                            wc.setPrecipitation(false);
                            break;
                        case "Clouds":
                            wc.setPrecipitation(false);
                            break;
                        case "Extreme":
                            wc.setPrecipitation(false);
                            break;
                        default:
                            wc.setPrecipitation(false);
                            break;
                    }

                    //Icon
                    wc.setIcon(getString("icon", weatherObj));
                    System.out.println("Data estratta: " + dataEstratta);
                    System.out.println("Data richiesta: " + startDate);
                    listWeather.add(wc);
                }
            }
            return listWeather;

        } catch (Exception ex) {
            throw new WeatherException("Error in loading weather");
        }
    }

}
