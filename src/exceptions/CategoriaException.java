/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;
/**
 * Exception thrown when an error occurs while managing a category.
 *
 * This custom exception is used to notify invalid operations,
 * inconsistent states, or unavailable category resources inside
 * the BudgetSight application.
 *
 * @author delbuer
 */
public class CategoriaException extends Exception{
    /**
     * Creates a new {@code CategoriaException} with the specified detail message.
     *
     * @param message the detail message describing the exception
     */
    public CategoriaException() {
    }

    public CategoriaException(String message) {
        super(message);
    }

    
    
}
