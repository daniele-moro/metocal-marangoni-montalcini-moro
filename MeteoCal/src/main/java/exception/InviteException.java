/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

/**
 *
 * @author m-daniele
 */
public class InviteException extends Exception {

    /**
     * Creates a new instance of <code>InviteException</code> without detail
     * message.
     */
    public InviteException() {
    }

    /**
     * Constructs an instance of <code>InviteException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public InviteException(String msg) {
        super(msg);
    }
}
