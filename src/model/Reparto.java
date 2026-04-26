package model;

import exceptions.*;
import java.util.ArrayList;

/**
 * Rappresenta un reparto aziendale con il proprio budget annuale
 * e la lista delle spese suddivise per categoria.
 */
public class Reparto {

    private String nomeReparto;
    private String responsabile;
    private double budgetAnnuale;
    private ArrayList<Categoria> categorie;

    // COSTRUTTORI

    public Reparto(String nomeReparto, String responsabile, double budgetAnnuale) {
        this.setNomeReparto(nomeReparto);
        this.setResponsabile(responsabile);
        this.setBudgetAnnuale(budgetAnnuale);
        this.categorie = new ArrayList<>();
    }

    public Reparto() {
        this.nomeReparto = "";
        this.responsabile = "";
        this.budgetAnnuale = 0;
        this.categorie = new ArrayList<>();
    }

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
     * Somma tutti gli importi delle categorie del reparto.
     */
    public double getTotaleSpeso() {
        double totale = 0;
        for (Categoria c : categorie) {
            totale += c.getImporto();
        }
        return totale;
    }

    /**
     * Budget annuale meno il totale speso.
     */
    public double getBudgetRimanente() {
        return budgetAnnuale - getTotaleSpeso();
    }

    /**
     * Restituisce true se il totale speso supera il budget annuale.
     */
    public boolean isSforato() {
        return getTotaleSpeso() > budgetAnnuale;
    }

    /**
     * Somma gli importi di tutte le voci che appartengono a una categoria specifica.
     * Utile per il grafico a torta e la dashboard "Top Categoria".
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

    // METODI CRUD sulle categorie

    /**
     * Aggiunge una voce di spesa al reparto.
     */
    public void aggiungiCategoria(Categoria c) {
        if (c == null) {
            throw new IllegalArgumentException("La categoria non può essere nulla");
        }
        categorie.add(c);
    }

    /**
     * Rimuove una voce di spesa dalla lista.
     * @throws CategoriaException se la categoria non è presente
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