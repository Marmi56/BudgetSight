package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Rappresenta una singola riga del file budget.csv.
 * Contiene tutti i campi grezzi letti dal file, prima che il Gestore
 * li organizzi in oggetti Reparto e Categoria.
 *
 * @author Computer
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

    // COSTRUTTORI

    /**
     * Costruttore completo, usato dal CSVReader.
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
     * Costruttore vuoto, usato per creare una voce da form (dialog).
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
     * Costruttore di copia.
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

    // GETTER

    public String getIdVoce() {
        return idVoce;
    }

    public String getNomeReparto() {
        return nomeReparto;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public double getBudgetAnnuale() {
        return budgetAnnuale;
    }

    public LocalDate getData() {
        return data;
    }

    public String getDataFormatted() {
        return data != null ? data.format(FORMATTER) : "";
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getImporto() {
        return importo;
    }

    // SETTER

    /**
     * L'id deve essere esattamente 4 cifre numeriche (es. "0001").
     */
    public void setIdVoce(String idVoce) {
        if (idVoce == null || idVoce.trim().isEmpty()) {
            throw new IllegalArgumentException("Id non può essere vuoto");
        }
        // ✅ || corretto: se lunghezza != 4 OPPURE non è solo cifre → errore
        if (idVoce.trim().length() != 4 || !idVoce.trim().matches("\\d+")) {
            throw new IllegalArgumentException("Id non valido (" + idVoce + "): deve essere 4 cifre numeriche");
        }
        this.idVoce = idVoce.trim();
    }

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

    public void setData(LocalDate data) {
        if (data == null) {
            throw new IllegalArgumentException("La data non può essere nulla");
        }
        this.data = data;
    }

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

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoria non può essere nulla o vuota");
        }
        this.categoria = categoria.trim();
    }

    public void setDescrizione(String descrizione) {
        if (descrizione != null && descrizione.length() > 100) {
            throw new IllegalArgumentException("La descrizione non può superare 100 caratteri");
        }
        this.descrizione = descrizione;
    }

    public void setImporto(double importo) {
        if (importo < 0) {
            throw new IllegalArgumentException("L'importo non può essere negativo");
        }
        this.importo = importo;
    }

    // METODI

    /**
     * Restituisce la riga nel formato CSV pronta per essere scritta su file. Utile per il salvamento del file
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

    @Override
    public String toString() {
        return "[" + idVoce + "] " + nomeReparto + " | " + categoria +
               " | €" + importo + " | " + getDataFormatted();
    }
}