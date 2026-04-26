package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Rappresenta una singola voce di spesa all'interno di un reparto,
 * con la categoria, la data, l'importo e la descrizione del servizio.
 */
public class Categoria {

    private String nomeCategoria;
    private LocalDate data;
    private double importo;
    private String descrizione;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // COSTRUTTORI

    public Categoria(String nomeCategoria, LocalDate data, double importo, String descrizione) {
        this.setNomeCategoria(nomeCategoria);
        this.setData(data);
        this.setImporto(importo);
        this.setDescrizione(descrizione);
    }

    public Categoria() {
        this.nomeCategoria = "";
        this.data = LocalDate.now();
        this.importo = 0;
        this.descrizione = "";
    }

    public Categoria(Categoria c) {
        this.nomeCategoria = c.nomeCategoria;
        this.data = c.data;
        this.importo = c.importo;
        this.descrizione = c.descrizione;
    }

    // GETTER

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public LocalDate getData() {
        return data;
    }

    public String getDataFormatted() {
        return data != null ? data.format(FORMATTER) : "";
    }

    public double getImporto() {
        return importo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    // SETTER

    public void setNomeCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della categoria non può essere vuoto");
        }
        this.nomeCategoria = nomeCategoria.trim();
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