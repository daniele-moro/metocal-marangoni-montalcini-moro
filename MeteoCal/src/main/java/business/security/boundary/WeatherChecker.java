/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import exception.WeatherException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
//import javax.persistence.PersistenceContext;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author m-daniele
 */
@Singleton
public class WeatherChecker {
    
    @EJB
            EventManager eventManager;
    
    @EJB
            SearchManager searchManager;
    
    @PersistenceContext
            EntityManager em;
    
    @EJB
            JsonPars jsonPars;
    
    @Schedule(month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "*/5", persistent = false)
    
    public void myTimer() {
        // System.out.println("Timer event: " + new Date());
        for (Event event : searchManager.findAllFutureEvents()) {
            WeatherCondition temp;
            try {
                temp = jsonPars.parsingWeather(event.getLatitude(), event.getLongitude(), event.getTimeStart());
                if (temp != null) {
                    eventManager.weatherUpdater(event, temp);
                }
                System.out.println("\nCambiate le weather forecast:");
                
            } catch (WeatherException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
    }
    
    @Schedule(month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "*/5", persistent = false)
    public void myTimer2() {
        // System.out.println("Timer event: " + new Date());
        for (Event event : searchManager.findAllFutureEvents()) {
            WeatherCondition temp;
            
            Date date = new Date();
            long currentDateMillisec = date.getTime();
            long eventStartMillisec = event.getTimeStart().getTime();
            
            long millisecDiff = eventStartMillisec - currentDateMillisec;
            //1 giorno medio = 1000*60*60*24 ms
            // = 86400000 ms
            int differenzaGiorni = (int) Math.floor(millisecDiff / 86400000.0);
            if(differenzaGiorni == 3) {
                eventManager.checkWeatherForecast(event);
            }
            if(differenzaGiorni == 1) {
                eventManager.checkWeatherOneDayBefore(event);
            }
        }
    }
    
    
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

}
