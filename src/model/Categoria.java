package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a single expense entry within a department.
 * Contains the category name, date, amount, and service description.
 * Each Categoria is associated with a specific Reparto.
 *
 * @author Computer
 * @since 1.0
 */
public class Categoria {

    private String nomeCategoria;
    private LocalDate data;
    private double importo;
    private String descrizione;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // CONSTRUCTORS

    /**
     * Constructs a Categoria with all required fields.
     *
     * @param nomeCategoria the category name (cannot be null or empty)
     * @param data the expense date (cannot be null)
     * @param importo the amount in euros (cannot be negative)
     * @param descrizione the service description (max 100 characters)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Categoria(String nomeCategoria, LocalDate data, double importo, String descrizione) {
        this.setNomeCategoria(nomeCategoria);
        this.setData(data);
        this.setImporto(importo);
        this.setDescrizione(descrizione);
    }

    /**
     * Constructs an empty Categoria with default values.
     * Default date is the current date, other fields are empty/zero.
     */
    public Categoria() {
        this.nomeCategoria = "";
        this.data = LocalDate.now();
        this.importo = 0;
        this.descrizione = "";
    }

    /**
     * Constructs a Categoria as a copy of another.
     * Creates a defensive copy of the provided Categoria instance.
     *
     * @param c the Categoria to copy
     */
    public Categoria(Categoria c) {
        this.nomeCategoria = c.nomeCategoria;
        this.data = c.data;
        this.importo = c.importo;
        this.descrizione = c.descrizione;
    }

    // GETTERS

    /**
     * Returns the category name.
     *
     * @return the category name
     */
    public String getNomeCategoria() {
        return nomeCategoria;
    }

    /**
     * Returns the expense date as a LocalDate object.
     *
     * @return the expense date
     */
    public LocalDate getData() {
        return data;
    }

    /**
     * Returns the expense date formatted as "dd-MM-yyyy".
     *
     * @return the formatted date string, or empty string if date is null
     */
    public String getDataFormatted() {
        return data != null ? data.format(FORMATTER) : "";
    }

    /**
     * Returns the expense amount in euros.
     *
     * @return the amount in euros
     */
    public double getImporto() {
        return importo;
    }

    /**
     * Returns the service description.
     *
     * @return the description (may be null or empty)
     */
    public String getDescrizione() {
        return descrizione;
    }

    // SETTERS

    /**
     * Sets the category name.
     *
     * @param nomeCategoria the category name (cannot be null or empty)
     * @throws IllegalArgumentException if the category name is null or empty
     */
    public void setNomeCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        this.nomeCategoria = nomeCategoria.trim();
    }

    /**
     * Sets the expense date from a LocalDate object.
     *
     * @param data the date (cannot be null)
     * @throws IllegalArgumentException if the date is null
     */
    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.data = data;
    }

    /**
     * Sets the expense date from a formatted string.
     * Expected format: "dd-MM-yyyy"
     *
     * @param data the date string in format "dd-MM-yyyy"
     * @throws IllegalArgumentException if the date string is null, empty, or invalid format
     */
    public void setData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        try {
            this.data = LocalDate.parse(data.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use DD-MM-YYYY");
        }
    }

    /**
     * Sets the expense amount in euros.
     *
     * @param importo the amount (cannot be negative)
     * @throws IllegalArgumentException if the amount is negative
     */
    public void setImporto(double importo) {
        if (importo < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.importo = importo;
    }

    /**
     * Sets the service description.
     *
     * @param descrizione the description (max 100 characters)
     * @throws IllegalArgumentException if the description exceeds 100 characters
     */
    public void setDescrizione(String descrizione) {
        if (descrizione != null && descrizione.length() > 100) {
            throw new IllegalArgumentException("Description cannot exceed 100 characters");
        }
        this.descrizione = descrizione;
    }

    // METHODS

    /**
     * Compares this Categoria with another object for equality.
     * Two Categoria objects are equal if they have the same name, date, and amount.
     *
     * @param obj the object to compare
     * @return true if both objects represent the same expense, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Categoria)) return false;
        Categoria altra = (Categoria) obj;
        return this.nomeCategoria.equalsIgnoreCase(altra.nomeCategoria)
                && this.data.equals(altra.data)
                && this.importo == altra.importo;
    }

    /**
     * Computes a hash code for this Categoria.
     * Based on the category name (lowercase) and date.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return (nomeCategoria.toLowerCase() + data.toString()).hashCode();
    }

    /**
     * Returns a string representation of this Categoria.
     * Format: "categoryName | date | €amount | description"
     *
     * @return the formatted string representation
     */
    @Override
    public String toString() {
        return nomeCategoria + " | " + getDataFormatted() + " | €" + importo + " | " + descrizione;
    }
}
