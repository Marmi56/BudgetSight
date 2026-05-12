package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a single budget entry line from the budget.csv file.
 * Contains raw data for department, category, date, and amount.
 * This is the primary unit of data read from and written to the CSV file.
 * The BudgetSight manager converts these entries into Reparto and Categoria objects.
 *
 * @author Computer
 * @since 1.0
 */
public class VoceBudget {

    private String idVoce;
    private String nomeReparto;
    private String responsabile;
    private double budgetAnnuale;
    private LocalDate data;
    private String categoria;
    private String descrizione;
    private double importo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String SEPARATORE = ",";

    // CONSTRUCTORS

    /**
     * Constructs a VoceBudget with all fields initialized.
     * Used by the CSV reader when loading from file.
     *
     * @param idVoce a 4-digit numeric identifier (e.g., "0001")
     * @param nomeReparto the department name (cannot be null or empty)
     * @param responsabile the manager name (cannot be null or empty)
     * @param budgetAnnuale the annual budget amount (cannot be negative)
     * @param data the expense date (cannot be null)
     * @param categoria the category name (cannot be null or empty)
     * @param descrizione the description text (max 100 characters)
     * @param importo the expense amount (cannot be negative)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public VoceBudget(String idVoce, String nomeReparto, String responsabile,
                      double budgetAnnuale, LocalDate data,
                      String categoria, String descrizione, double importo) {
        this.setIdVoce(idVoce);
        this.setNomeReparto(nomeReparto);
        this.setResponsabile(responsabile);
        this.setBudgetAnnuale(budgetAnnuale);
        this.setData(data);
        this.setCategoria(categoria);
        this.setDescrizione(descrizione);
        this.setImporto(importo);
    }

    /**
     * Constructs an empty VoceBudget with default values.
     * Used when creating a new entry from a form dialog.
     * All string fields are empty, date is today, numeric fields are zero.
     */
    public VoceBudget() {
        this.idVoce = "";
        this.nomeReparto = "";
        this.responsabile = "";
        this.budgetAnnuale = 0;
        this.data = LocalDate.now();
        this.categoria = "";
        this.descrizione = "";
        this.importo = 0;
    }

    /**
     * Constructs a VoceBudget as a copy of another.
     * Creates a defensive copy of all fields.
     *
     * @param v the VoceBudget to copy
     */
    public VoceBudget(VoceBudget v) {
        this.idVoce = v.idVoce;
        this.nomeReparto = v.nomeReparto;
        this.responsabile = v.responsabile;
        this.budgetAnnuale = v.budgetAnnuale;
        this.data = v.data;
        this.categoria = v.categoria;
        this.descrizione = v.descrizione;
        this.importo = v.importo;
    }

    // GETTERS

    /**
     * Returns the unique voice identifier (4-digit number).
     *
     * @return the ID as a string (e.g., "0001")
     */
    public String getIdVoce() {
        return idVoce;
    }

    /**
     * Returns the department name.
     *
     * @return the department name
     */
    public String getNomeReparto() {
        return nomeReparto;
    }

    /**
     * Returns the manager name.
     *
     * @return the manager name
     */
    public String getResponsabile() {
        return responsabile;
    }

    /**
     * Returns the annual budget amount.
     *
     * @return the budget in euros
     */
    public double getBudgetAnnuale() {
        return budgetAnnuale;
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
     * Returns the category name.
     *
     * @return the category name
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Returns the description text.
     *
     * @return the description (may be null or empty)
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Returns the expense amount.
     *
     * @return the amount in euros
     */
    public double getImporto() {
        return importo;
    }

    // SETTERS

    /**
     * Sets the unique voice identifier.
     * Must be exactly 4 numeric digits (e.g., "0001").
     *
     * @param idVoce the 4-digit ID
     * @throws IllegalArgumentException if the ID is not 4 numeric digits
     */
    public void setIdVoce(String idVoce) {
        if (idVoce == null || idVoce.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (idVoce.trim().length() != 4 || !idVoce.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Invalid ID (" + idVoce + "): must be 4 numeric digits");
        }
        this.idVoce = idVoce.trim();
    }

    /**
     * Sets the department name.
     *
     * @param nomeReparto the department name (cannot be null or empty)
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setNomeReparto(String nomeReparto) {
        if (nomeReparto == null || nomeReparto.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name is invalid");
        }
        this.nomeReparto = nomeReparto.trim();
    }

    /**
     * Sets the manager name.
     *
     * @param responsabile the manager name (cannot be null or empty)
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setResponsabile(String responsabile) {
        if (responsabile == null || responsabile.trim().isEmpty()) {
            throw new IllegalArgumentException("Manager name is invalid");
        }
        this.responsabile = responsabile.trim();
    }

    /**
     * Sets the annual budget amount.
     *
     * @param budgetAnnuale the budget (cannot be negative)
     * @throws IllegalArgumentException if the budget is negative
     */
    public void setBudgetAnnuale(double budgetAnnuale) {
        if (budgetAnnuale < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");
        }
        this.budgetAnnuale = budgetAnnuale;
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
     * Sets the category name.
     *
     * @param categoria the category name (cannot be null or empty)
     * @throws IllegalArgumentException if the name is null or empty
     */
    public void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.categoria = categoria.trim();
    }

    /**
     * Sets the description text.
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

    /**
     * Sets the expense amount.
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

    // METHODS

    /**
     * Converts this voice to CSV format for file output.
     * Useful for saving the voice back to the CSV file.
     *
     * @return the CSV-formatted string ready to write to file
     */
    public String toCSV() {
        return idVoce + SEPARATORE +
               nomeReparto + SEPARATORE +
               responsabile + SEPARATORE +
               budgetAnnuale + SEPARATORE +
               getDataFormatted() + SEPARATORE +
               categoria + SEPARATORE +
               (descrizione != null ? descrizione : "") + SEPARATORE +
               importo;
    }

    /**
     * Returns a string representation of this voice.
     * Format: "[id] departmentName | category | €amount | date"
     *
     * @return the formatted string representation
     */
    @Override
    public String toString() {
        return "[" + idVoce + "] " + nomeReparto + " | " + categoria +
               " | €" + importo + " | " + getDataFormatted();
    }
}
