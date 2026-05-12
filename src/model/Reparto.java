package model;

import exceptions.*;
import java.util.ArrayList;
/**
 * Represents a company department with its annual budget
 * and the related expense categories.
 *
 * The class provides calculation utilities and CRUD operations
 * for managing category entries.
 *
 * @author delbuer
 * @see Categoria
 */
public class Reparto {
    /**
     * Department name.
     */
    private String nomeReparto;
    /**
     * Department manager.
     */
    private String responsabile;
    /**
     * Annual department budget.
     */
    private double budgetAnnuale;
    /**
     * List of associated categories.
     */
    private ArrayList<Categoria> categorie;
     /**
     * Creates a new department with initialized values.
     *
     * @param nomeReparto the department name
     * @param responsabile the department manager
     * @param budgetAnnuale the annual budget
     */

    // COSTRUTTORI

    public Reparto(String nomeReparto, String responsabile, double budgetAnnuale) {
        this.setNomeReparto(nomeReparto);
        this.setResponsabile(responsabile);
        this.setBudgetAnnuale(budgetAnnuale);
        this.categorie = new ArrayList<>();
    }
        /**
     * Creates an empty {@code Reparto} object.
     */

    public Reparto() {
        this.nomeReparto = "";
        this.responsabile = "";
        this.budgetAnnuale = 0;
        this.categorie = new ArrayList<>();
    }
    /**
     * Calculates the total amount spent by the department.
     *
     * @return the total spent amount
     */

    public Reparto(Reparto r) {
        this.nomeReparto = r.nomeReparto;
        this.responsabile = r.responsabile;
        this.budgetAnnuale = r.budgetAnnuale;
        this.categorie = new ArrayList<>(r.categorie); // copia difensiva
    }

    // GETTER

    public String getNomeReparto() {
        return nomeReparto;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public double getBudgetAnnuale() {
        return budgetAnnuale;
    }

    public ArrayList<Categoria> getCategorie() {
        return categorie;
    }

    // SETTER

    public void setNomeReparto(String nomeReparto) {
        if (nomeReparto == null || nomeReparto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome del reparto non valido");
        }
        this.nomeReparto = nomeReparto.trim();
    }

    public void setResponsabile(String responsabile) {
        if (responsabile == null || responsabile.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome del responsabile non valido");
        }
        this.responsabile = responsabile.trim();
    }

    public void setBudgetAnnuale(double budgetAnnuale) {
        if (budgetAnnuale < 0) {
            throw new IllegalArgumentException("Il budget non può essere negativo");
        }
        this.budgetAnnuale = budgetAnnuale;
    }

    // METODI DI CALCOLO (per le dashboard)

    /**
     * Calculates the total amount spent by the department.
     *
     * @return the total spent amount
     */
    public double getTotaleSpeso() {
        double totale = 0;
        for (Categoria c : categorie) {
            totale += c.getImporto();
        }
        return totale;
    }

    /**
     * Returns the remaining annual budget.
     *
     * @return the remaining budget amount
     */
    public double getBudgetRimanente() {
        return budgetAnnuale - getTotaleSpeso();
    }

    /**
     * Checks whether the department exceeded the annual budget.
     *
     * @return {@code true} if the budget is exceeded, otherwise {@code false}
     */
    public boolean isSforato() {
        return getTotaleSpeso() > budgetAnnuale;
    }

   double getSpesaPerCategoria(String nomeCategoria) {
        double totale = 0;
        for (Categoria c : categorie) {
            if (c.getNomeCategoria().equalsIgnoreCase(nomeCategoria)) {
                totale += c.getImporto();
            }
        }
        return totale;
    }

    // METODI CRUD sulle categorie
/**
     * Adds a category entry to the department.
     *
     * @param c the category to add
     * @throws IllegalArgumentException if the category is null
     */
    public void aggiungiCategoria(Categoria c) {
        if (c == null) {
            throw new IllegalArgumentException("La categoria non può essere nulla");
        }
        categorie.add(c);
    }

 /**
     * Removes a category entry from the department.
     *
     * @param c the category to remove
     * @throws CategoriaException if the category is not found
     * @throws IllegalArgumentException if the category is null
     */
    public void rimuoviCategoria(Categoria c) throws CategoriaException {
        if (c == null) {
            throw new IllegalArgumentException("La categoria non può essere nulla");
        }
        if (!categorie.remove(c)) {
            throw new CategoriaException("Categoria non trovata nel reparto " + nomeReparto);
        }
    }

    /**
     * Cerca tutte le voci di una certa categoria per nome.
     * Restituisce una lista perché nello stesso reparto possono esserci
     * più spese della stessa categoria in date diverse.
     */
    public ArrayList<Categoria> trovaPerNomeCategoria(String nomeCategoria) throws CategoriaException {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome categoria non valido");
        }
        ArrayList<Categoria> risultati = new ArrayList<>();
        for (Categoria c : categorie) {
            if (c.getNomeCategoria().equalsIgnoreCase(nomeCategoria)) {
                risultati.add(c);
            }
        }
        if (risultati.isEmpty()) {
            throw new CategoriaException("Nessuna spesa trovata per la categoria: " + nomeCategoria);
        }
        return risultati;
    }

    @Override
    public String toString() {
        return nomeReparto + " | Responsabile: " + responsabile
                + " | Budget: €" + budgetAnnuale
                + " | Speso: €" + getTotaleSpeso()
                + " | Rimanente: €" + getBudgetRimanente();
    }
}