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

    /**
     * This method is called every 12 hours to update the weather forecast for all the future events
     * 
     **/
    @Schedule(hour = "8, 20", persistent = false)
    public void refreshWeatherForecast() {
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

    /**
     * This method is called once a day (e.g., every midnight) to check the weather forecast
     * for the events that will be performed three days or one day after the invocation
     **/
    @Schedule(hour = "20", persistent = false)
    public void checkAcceptedWeatherConditions() {
        /**
         * Construction of two date, one is one day after now, the other is
         * three days after now
        *
         */
        GregorianCalendar oneDayAfter = new GregorianCalendar();
        oneDayAfter.roll(Calendar.DAY_OF_YEAR, 1);
        GregorianCalendar threeDayAfter = new GregorianCalendar();
        threeDayAfter.roll(Calendar.DAY_OF_YEAR, 3);
        for (Event event : searchManager.findAllFutureEvents()) {
            if (event.isOutdoor() && event.getAcceptedWeatherConditions() != null) {

                //If the event is THREE days after now
                if (event.getTimeStart().getDate() == threeDayAfter.getTime().getDate()
                        && event.getTimeStart().getMonth() == threeDayAfter.getTime().getMonth()
                        && event.getTimeStart().getYear() == threeDayAfter.getTime().getYear()) {
                    //checks if there's some discrepancy between the forecasted and the accepted weather
                    //if true it sends an email and a notification to the organizer of the event
                    eventManager.checkWeatherForecast(event);
                }

                //if the event is ONE day after now
                if (event.getTimeStart().getDate() == oneDayAfter.getTime().getDate()
                        && event.getTimeStart().getMonth() == oneDayAfter.getTime().getMonth()
                        && event.getTimeStart().getYear() == oneDayAfter.getTime().getYear()) {
                    //Checks if there's some discrepancy between the forecasted and the accepted weather
                    //if true it sends an email and a notification to each participant
                    eventManager.checkWeatherOneDayBefore(event);
                }
            }
        }
    }
}
