/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.WeatherCondition;
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
    
    @PersistenceContext
    EntityManager em;
    
    @EJB
    JsonPars jsonPars;

    @Schedule(month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "*", second = "*/5", persistent = false)
    
    public void myTimer() {
       // System.out.println("Timer event: " + new Date());
        for(Event event : eventManager.getAllEvents()) {
            WeatherCondition temp;
            
            try {
                temp = jsonPars.parsingWeather(event.getLatitude(), event.getLongitude(), event.getTimeStart());
                Query findCondition = em.createQuery("SELECT w FROM WeatherCondition w WHERE w.id = ?1"); 
                findCondition.setParameter(1, event.getWeatherForecast().getId());
                WeatherCondition w = (WeatherCondition) findCondition.getResultList().get(0);
                w.setPrecipitation(temp.getPrecipitation());
                w.setTemperature(temp.getTemperature());
                w.setIcon(temp.getIcon());
                w.setWind(temp.getWind());
                em.merge(w);
                System.out.println("\nCambiate le weather forecast:");
                System.out.println(event.getWeatherForecast().toString());
            } catch (JSONException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
