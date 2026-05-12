/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;

/**
 *
 * @author Computer
 */
public class FileException extends Exception{
    /**
 * Exception thrown when an error occurs during file operations.
 *
 * This exception is used by the application to report issues related
 * to reading, writing, loading, or validating external files.
 *
 * @author delbuer
 */
    /**
     * Creates a new {@code FileException} with the specified detail message.
     *
     * @param message the detail message describing the exception
     */

    public FileException() {
        super();
    }

    public FileException(String message) {
        super(message);
    }
    
}
