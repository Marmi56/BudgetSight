package model;

import exceptions.*;
import java.util.ArrayList;

/**
 * Represents a corporate department with annual budget and expense tracking.
 * Contains a list of expense categories and provides calculation methods
 * for budget analysis and spending summaries.
 *
 * @author Computer
 * @since 1.0
 */
public class Reparto {

    private String nomeReparto;
    private String responsabile;
    private double budgetAnnuale;
    private ArrayList<Categoria> categorie;

    // CONSTRUCTORS

    /**
     * Constructs a Reparto with the specified details.
     *
     * @param nomeReparto the department name (cannot be null or empty)
     * @param responsabile the department manager name (cannot be null or empty)
     * @param budgetAnnuale the annual budget amount (cannot be negative)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Reparto(String nomeReparto, String responsabile, double budgetAnnuale) {
        this.setNomeReparto(nomeReparto);
        this.setResponsabile(responsabile);
        this.setBudgetAnnuale(budgetAnnuale);
        this.categorie = new ArrayList<>();
    }

    /**
     * Constructs an empty Reparto with default values.
     * All string fields are empty, budget is zero, and category list is empty.
     */
    public Reparto() {
        this.nomeReparto = "";
        this.responsabile = "";
        this.budgetAnnuale = 0;
        this.categorie = new ArrayList<>();
    }

    /**
     * Constructs a Reparto as a copy of another.
     * Creates a defensive copy of the category list.
     *
     * @param r the Reparto to copy
     */
    public Reparto(Reparto r) {
        this.nomeReparto = r.nomeReparto;
        this.responsabile = r.responsabile;
        this.budgetAnnuale = r.budgetAnnuale;
        this.categorie = new ArrayList<>(r.categorie); // defensive copy
    }

    // GETTERS

    /**
     * Returns the department name.
     *
     * @return the department name
     */
    public String getNomeReparto() {
        return nomeReparto;
    }

    /**
     * Returns the department manager name.
     *
     * @return the manager name
     */
    public String getResponsabile() {
        return responsabile;
    }

    /**
     * Returns the annual budget amount.
     *
     * @return the annual budget in euros
     */
    public double getBudgetAnnuale() {
        return budgetAnnuale;
    }

    /**
     * Returns the list of expense categories in this department.
     *
     * @return the ArrayList of Categoria objects
     */
    public ArrayList<Categoria> getCategorie() {
        return categorie;
    }

    // SETTERS

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
     * Sets the department manager name.
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
     * @param budgetAnnuale the budget amount (cannot be negative)
     * @throws IllegalArgumentException if the budget is negative
     */
    public void setBudgetAnnuale(double budgetAnnuale) {
        if (budgetAnnuale < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");
        }
        this.budgetAnnuale = budgetAnnuale;
    }

    // CALCULATION METHODS (for dashboards)

    /**
     * Calculates the total amount spent in this department.
     * Sums all expense amounts from all categories.
     *
     * @return the total amount spent in euros
     */
    public double getTotaleSpeso() {
        double totale = 0;
        for (Categoria c : categorie) {
            totale += c.getImporto();
        }
        return totale;
    }

    /**
     * Calculates the remaining budget.
     * Returns the annual budget minus the total spent amount.
     *
     * @return the remaining budget in euros (may be negative if overspent)
     */
    public double getBudgetRimanente() {
        return budgetAnnuale - getTotaleSpeso();
    }

    /**
     * Checks if the budget has been exceeded.
     *
     * @return true if total spent exceeds annual budget, false otherwise
     */
    public boolean isSforato() {
        return getTotaleSpeso() > budgetAnnuale;
    }

    /**
     * Calculates the total spending for a specific category name.
     * Useful for pie charts and "Top Category" dashboards.
     *
     * @param nomeCategoria the category name to search for
     * @return the total amount spent in that category across all dates
     */
    public double getSpesaPerCategoria(String nomeCategoria) {
        double totale = 0;
        for (Categoria c : categorie) {
            if (c.getNomeCategoria().equalsIgnoreCase(nomeCategoria)) {
                totale += c.getImporto();
            }
        }
        return totale;
    }

    // CRUD METHODS for categories

    /**
     * Adds an expense entry to this department.
     *
     * @param c the Categoria to add (cannot be null)
     * @throws IllegalArgumentException if the category is null
     */
    public void aggiungiCategoria(Categoria c) {
        if (c == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        categorie.add(c);
    }

    /**
     * Removes an expense entry from this department.
     *
     * @param c the Categoria to remove (cannot be null)
     * @throws IllegalArgumentException if the category is null
     * @throws CategoriaException if the category is not found in this department
     */
    public void rimuoviCategoria(Categoria c) throws CategoriaException {
        if (c == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (!categorie.remove(c)) {
            throw new CategoriaException("Category not found in department " + nomeReparto);
        }
    }

    /**
     * Searches for all expense entries with a specific category name.
     * Multiple entries may have the same category name on different dates.
     *
     * @param nomeCategoria the category name to search for
     * @return an ArrayList of matching Categoria objects
     * @throws IllegalArgumentException if the category name is null or empty
     * @throws CategoriaException if no expenses are found for that category
     */
    public ArrayList<Categoria> trovaPerNomeCategoria(String nomeCategoria) throws CategoriaException {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is invalid");
        }
        ArrayList<Categoria> risultati = new ArrayList<>();
        for (Categoria c : categorie) {
            if (c.getNomeCategoria().equalsIgnoreCase(nomeCategoria)) {
                risultati.add(c);
            }
        }
        if (risultati.isEmpty()) {
            throw new CategoriaException("No expenses found for category: " + nomeCategoria);
        }
        return risultati;
    }

    /**
     * Returns a string representation of this Reparto.
     * Format: "departmentName | Manager: name | Budget: €amount | Spent: €amount | Remaining: €amount"
     *
     * @return the formatted string representation
     */
    @Override
    public String toString() {
        return nomeReparto + " | Manager: " + responsabile
                + " | Budget: €" + budgetAnnuale
                + " | Spent: €" + getTotaleSpeso()
                + " | Remaining: €" + getBudgetRimanente();
    }
}
