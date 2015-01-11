/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author Daniele
 */
public class WeatherException extends Exception {

    /**
     * Creates a new instance of <code>WeatherException</code> without detail
     * message.
     */
    public WeatherException() {
    }

    /**
     * Constructs an instance of <code>WeatherException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public WeatherException(String msg) {
        super(msg);
    }
}
