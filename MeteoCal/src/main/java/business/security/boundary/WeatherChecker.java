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
                    
                    if (event.getWeatherForecast() != null) {
                        WeatherCondition w;
                        Query findCondition = em.createQuery("SELECT w FROM WeatherCondition w WHERE w.id = ?1");
                        findCondition.setParameter(1, event.getWeatherForecast().getId());
                        w = (WeatherCondition) findCondition.getResultList().get(0);
                        w.setPrecipitation(temp.getPrecipitation());
                        w.setTemperature(temp.getTemperature());
                        w.setIcon(temp.getIcon());
                        w.setWind(temp.getWind());
                        em.merge(w);
                    } else{
                        em.persist(temp);
                        event.setWeatherForecast(temp);
                        em.merge(event);
                        
                    }
                }
                System.out.println("\nCambiate le weather forecast:");
                //System.out.println(event.getWeatherForecast().toString());

            } catch (JSONException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
