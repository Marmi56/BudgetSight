package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a single expense category entry associated with a department.
 *
 * Each category contains information about the expense date,
 * amount, and service description.
 *
 * @author delbuer
 */
public class Categoria {
    /**
     * Name of the category.
     */
    private String nomeCategoria;
     /**
     * Date associated with the expense.
     */
    private LocalDate data;
    /**
     * Expense amount.
     */
    private double importo;
    /**
     * Description of the service or expense.
     */
    private String descrizione;
    /**
     * Formatter used for date conversion.
     */

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // COSTRUTTORI
    /**
     * Creates a new {@code Categoria} object with all fields initialized.
     *
     * @param nomeCategoria the category name
     * @param data the expense date
     * @param importo the expense amount
     * @param descrizione the expense description
     */

    public Categoria(String nomeCategoria, LocalDate data, double importo, String descrizione) {
        this.setNomeCategoria(nomeCategoria);
        this.setData(data);
        this.setImporto(importo);
        this.setDescrizione(descrizione);
    }
    /**
     * Creates an empty {@code Categoria} object with default values.
     */
    public Categoria() {
        this.nomeCategoria = "";
        this.data = LocalDate.now();
        this.importo = 0;
        this.descrizione = "";
    }
    /**
     * Creates a copy of an existing {@code Categoria} object.
     *
     * @param c the category object to copy
     */

    public Categoria(Categoria c) {
        this.nomeCategoria = c.nomeCategoria;
        this.data = c.data;
        this.importo = c.importo;
        this.descrizione = c.descrizione;
    }
    /**
     * Returns the category name.
     *
     * @return the category name
     */

    // GETTER
    public String getNomeCategoria() {
        return nomeCategoria;
    }
      /**
     * Returns the expense date.
     *
     * @return the expense date
     */
    public LocalDate getData() {
        return data;
    }
      /**
     * Returns the formatted expense date.
     *
     * @return the formatted date as dd-MM-yyyy
     */
    public String getDataFormatted() {
        return data != null ? data.format(FORMATTER) : "";
    }
    /**
     * Returns the expense amount.
     *
     * @return the expense amount
     */

    public double getImporto() {
        return importo;
    }
    /**
     * Returns the expense description.
     *
     * @return the expense description
     */

    public String getDescrizione() {
        return descrizione;
    }
     /**
     * Sets the category name.
     *
     * @param nomeCategoria the category name to set
     * @throws IllegalArgumentException if the category name is null or empty
     */

    // SETTER

    public void setNomeCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della categoria non può essere vuoto");
        }
        this.nomeCategoria = nomeCategoria.trim();
    }
    /**
     * Sets the expense date.
     *
     * @param data the date to set
     * @throws IllegalArgumentException if the date is null
     */

    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("La data non può essere nulla");
        }
        this.data = data;
    }
    
    /**
     * Sets the expense date from a formatted string.
     *
     * @param data the formatted date string
     * @throws IllegalArgumentException if the date is invalid or empty
     */

    public void setData(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("La data non può essere nulla o vuota");
        }
        try {
            this.data = LocalDate.parse(data.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato data non valido. Usa GG-MM-YYYY");
        }
    }

    public void setImporto(double importo) {
        if (importo < 0) {
            throw new IllegalArgumentException("L'importo non può essere negativo");
        }
        this.importo = importo;
    }

    public void setDescrizione(String descrizione) {
        if (descrizione != null && descrizione.length() > 100) {
            throw new IllegalArgumentException("La descrizione non può superare 100 caratteri");
        }
        this.descrizione = descrizione;
    }

    // METODI

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Categoria)) return false;
        Categoria altra = (Categoria) obj;
        return this.nomeCategoria.equalsIgnoreCase(altra.nomeCategoria)
                && this.data.equals(altra.data)
                && this.importo == altra.importo;
    }

    @Override
    public int hashCode() {
        return (nomeCategoria.toLowerCase() + data.toString()).hashCode();
    }

    @Override
    public String toString() {
        return nomeCategoria + " | " + getDataFormatted() + " | €" + importo + " | " + descrizione;
    }
}