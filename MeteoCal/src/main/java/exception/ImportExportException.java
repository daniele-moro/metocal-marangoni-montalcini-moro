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
public class ImportExportException extends Exception {

    /**
     * Creates a new instance of <code>ImportExportException</code> without
     * detail message.
     */
    public ImportExportException() {
    }

    /**
     * Constructs an instance of <code>ImportExportException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ImportExportException(String msg) {
        super(msg);
    }
}
