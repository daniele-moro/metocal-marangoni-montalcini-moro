/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.control;

import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import exception.WeatherException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    @Schedule(month = "*", hour = "*", dayOfMonth = "*", year = "*", minute = "1", second = "0", persistent = false)
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
        GregorianCalendar oneDayAfter = new GregorianCalendar();
        oneDayAfter.roll(Calendar.DAY_OF_YEAR, 1);
        GregorianCalendar threeDayAfter = new GregorianCalendar();
        threeDayAfter.roll(Calendar.DAY_OF_YEAR, 3);
        for (Event event : searchManager.findAllFutureEvents()) {
            if (event.isOutdoor() && event.getAcceptedWeatherConditions() != null) {
                WeatherCondition temp;

                if( event.getTimeStart().getDate()==threeDayAfter.getTime().getDate()
                        && event.getTimeStart().getMonth()==threeDayAfter.getTime().getMonth()
                        && event.getTimeStart().getYear()==threeDayAfter.getTime().getYear() 
                        ){
                    eventManager.checkWeatherForecast(event);
                }
                if( event.getTimeStart().getDate()==oneDayAfter.getTime().getDate()
                        && event.getTimeStart().getMonth()==oneDayAfter.getTime().getMonth()
                        && event.getTimeStart().getYear()==oneDayAfter.getTime().getYear() 
                        ){
                    eventManager.checkWeatherOneDayBefore(event);
                }
            }
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
