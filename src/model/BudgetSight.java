package model;

import exceptions.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Main application manager for the BudgetSight system.
 *
 * This class acts as the single access point for the graphical user interface,
 * managing CSV reading and writing operations, budget entries, departments,
 * and all calculation utilities required by the dashboards.
 *
 * @author delbuer
 * @see VoceBudget
 * @see Reparto
 * @see Categoria
 */
public class BudgetSight {
    /**
     * List containing all budget entries.
     */
    private ArrayList<VoceBudget> elenco;
    /**
     * Map containing all departments indexed by name.
     */
    private HashMap<String, Reparto> reparti;
    /**
     * CSV separator character.
     */
    private static final String SEPARATORE = ",";
     /**
     * Formatter used for date conversion.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /**
     * Creates a new {@code BudgetSight} manager.
     */
    // COSTRUTTORE

    public BudgetSight() {
        this.elenco = new ArrayList<>();
        this.reparti = new HashMap<>();
    }
    /**
     * Loads and parses the CSV file.
     *
     * The method populates the internal collections of
     * {@link VoceBudget} and {@link Reparto} objects.
     *
     * @param file the CSV file to load
     * @throws IOException if the file cannot be read
     */

    // =========================================================
    // CSV — LETTURA E SCRITTURA
    // =========================================================

    /**
     * Legge il file CSV e popola elenco e reparti.
     * Ignora la prima riga (intestazione).
     *
     * @param file il file budget.csv da leggere
     * @throws IOException se il file non è leggibile
     */
    public void caricaCSV(File file) throws IOException {
        elenco.clear();
        reparti.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            boolean primaRiga = true;

            while ((riga = br.readLine()) != null) {
                if (primaRiga) {
                    //salta intestazione
                    primaRiga = false;
                    continue;
                }
                if (riga.trim().isEmpty()) {
                    //salta riga vuota del file di prova
                    continue;
                }

                try {
                    VoceBudget v = parseRiga(riga);
                    elenco.add(v);
                    aggiungiAReparti(v);
                } catch (IllegalArgumentException e) {
                    // riga malformata: la saltiamo senza crashare (exception-safe)
                    System.err.println("Riga ignorata: " + riga + " — " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            System.err.println("Errore nell'apertura del file");
        }
    }
     /**
     * Validates the output CSV path.
     *
     * If the provided path is invalid, a default filename
     * named {@code outputFile.csv} is automatically generated.
     *
     * @param percorsoInput the input path to validate
     * @return the validated path string
     */
    
    
    
    public static String validaPercorso(String percorsoInput) {

        if (percorsoInput == null || percorsoInput.trim().isEmpty()) {
            return "outputFile.csv";
        }

        percorsoInput = percorsoInput.trim();

        // Se termina con slash o backslash -> è una directory
        if (percorsoInput.endsWith("/") || percorsoInput.endsWith("\\")) {
            return percorsoInput + "outputFile.csv";
        }

        Path path = Paths.get(percorsoInput);

        // Prendo l'ultimo elemento del path
        Path fileName = path.getFileName();

        // Caso limite
        if (fileName == null) {
            return percorsoInput + "\\outputFile.csv";
        }

        String nomeFinale = fileName.toString();

        // Se non ha estensione .csv assumo che sia una cartella
        if (!nomeFinale.toLowerCase().endsWith(".csv")) {
            return percorsoInput + "\\outputFile.csv";
        }

        // Path già valido
        return percorsoInput;
    }

    /**
     * Scrive tutte le VoceBudget nel file CSV, sovrascrivendolo.
     *
     * @param file il file di destinazione
     * @throws IOException se il file non è scrivibile
     * @throws FileException se la struttura dati è vuota e non c'è nulla da scrivere
     */
    public void salvaCSV(File file) throws IOException, FileException {
        if (this.elenco.isEmpty()) {
            throw new FileException("Impossibile scrivere il file, la struttura dati è vuota.");
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // intestazione
            bw.write("idVoce,nomeReparto,responsabile,budgetAnnuale,data,categoria,descrizione,importo");
            bw.newLine();

            for (VoceBudget v : elenco) {
                bw.write(v.toCSV());
                bw.newLine();
            }
        }
    }
    
    /**
     * Scrive tutte le VoceBudget nella path selezionata.
     *
     * @param path è il percorso di destinazione
     * @throws IOException se il file non è scrivibile
     * @throws FileException se la struttura dati è vuota e non c'è nulla da scrivere oppure se il file non viene creato correttamente
     */
    public void salvaCSV(String path) throws FileException, IOException {
        if (this.elenco.isEmpty()) {
            throw new FileException("Impossibile scrivere il file, la struttura dati è vuota.");
        }
        //validazione della path
        path = validaPercorso(path);
        
        File file = new File(path);
        if (!file.createNewFile()) {
            throw new FileException("Impossibile creare il file, controlla il percorso: potrebbe essere già esistente il file.");
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // intestazione
            bw.write("idVoce,nomeReparto,responsabile,budgetAnnuale,data,categoria,descrizione,importo");
            bw.newLine();

            for (VoceBudget v : elenco) {
                bw.write(v.toCSV());
                bw.newLine();
            }
        }
    }
    /**
     * Converte una riga CSV in un oggetto VoceBudget.
     */
    private VoceBudget parseRiga(String riga) {
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length < 8) {
            throw new IllegalArgumentException("Numero di campi insufficiente");
        }

        String idVoce       = campi[0].trim();
        String nomeReparto  = campi[1].trim();
        String responsabile = campi[2].trim();
        double budgetAnnuale;
        LocalDate data;
        String categoria    = campi[5].trim();
        String descrizione  = campi[6].trim();
        double importo;

        try {
            budgetAnnuale = Double.parseDouble(campi[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Budget annuale non valido: " + campi[3]);
        }

        try {
            data = LocalDate.parse(campi[4].trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data non valida: " + campi[4]);
        }

        try {
            importo = Double.parseDouble(campi[7].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Importo non valido: " + campi[7]);
        }

        return new VoceBudget(idVoce, nomeReparto, responsabile,
                              budgetAnnuale, data, categoria, descrizione, importo);
    }

    /**
     * Inserisce una VoceBudget nella HashMap dei reparti.
     * Se il reparto non esiste ancora, lo crea.
     */
    private void aggiungiAReparti(VoceBudget v) {
        String nomeReparto = v.getNomeReparto();

        if (!reparti.containsKey(nomeReparto)) {
            Reparto r = new Reparto(nomeReparto, v.getResponsabile(), v.getBudgetAnnuale());
            reparti.put(nomeReparto, r);
        }

        Categoria c = new Categoria(v.getCategoria(), v.getData(), v.getImporto(), v.getDescrizione());
        reparti.get(nomeReparto).aggiungiCategoria(c);
    }

    // =========================================================
    // METODI PER LE 4 DASHBOARD
    // =========================================================

    /**
     * DASHBOARD 1 — Somma dei budget annuali di tutti i reparti.
     */
    public double getBudgetTotale() {
        double totale = 0;
        for (Reparto r : reparti.values()) {
            totale += r.getBudgetAnnuale();
        }
        return totale;
    }

    /**
     * DASHBOARD 2 — Somma di tutti gli importi spesi.
     */
    public double getSpeseTotali() {
        double totale = 0;
        for (VoceBudget v : elenco) {
            totale += v.getImporto();
        }
        return totale;
    }

    /**
     * DASHBOARD 3 — Lista dei reparti che hanno sforato il budget.
     */
    public ArrayList<Reparto> getSforamentiAttivi() {
        ArrayList<Reparto> sforati = new ArrayList<>();
        for (Reparto r : reparti.values()) {
            if (r.isSforato()) {
                sforati.add(r);
            }
        }
        return sforati;
    }
    
    public double getTotaleSforamento() {
        double totale = 0;
        for (Reparto r : reparti.values()) {
            if (r.isSforato()) {
                totale += r.getTotaleSpeso() - r.getBudgetAnnuale();
            }
        }
        return totale;
    }

    /**
     * DASHBOARD 4 — Nome della categoria con la spesa totale più alta
     * sommando su tutti i reparti.
     */
    public String getCategoriaMaxSpesa() {
        HashMap<String, Double> totPerCategoria = new HashMap<>();

        for (VoceBudget v : elenco) {
            String cat = v.getCategoria();
            totPerCategoria.put(cat, totPerCategoria.getOrDefault(cat, 0.0) + v.getImporto());
        }

        String categoriaMax = "";
        double max = -1;
        for (Map.Entry<String, Double> entry : totPerCategoria.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                categoriaMax = entry.getKey();
            }
        }
        return categoriaMax;
    }
    
    public Reparto getRepartoMaxSpesa() {
        Reparto max = null;
        for (Reparto r : reparti.values()) {
            if (max == null || r.getTotaleSpeso() > max.getTotaleSpeso()) {
                max = r;
            }
        }
        return max;
    }

    // =========================================================
    // CRUD — INSERIMENTO, MODIFICA, ELIMINAZIONE
    // =========================================================

    /**
     * Aggiunge una nuova voce all'elenco e aggiorna la HashMap dei reparti.
     *
     * @param v la voce da inserire
     */
    public void inserisciVoce(VoceBudget v) {
        if (v == null) {
            throw new IllegalArgumentException("La voce non può essere nulla");
        }
        elenco.add(v);
        aggiungiAReparti(v);
    }

    /**
     * Sostituisce una voce esistente (stesso idVoce) con quella aggiornata,
     * poi ricostruisce la HashMap dei reparti.
     *
     * @param vAggiornata la voce con i nuovi valori
     * @throws NoSuchElementException se l'id non viene trovato
     */
    public void modificaVoce(VoceBudget vAggiornata) {
        if (vAggiornata == null) {
            throw new IllegalArgumentException("La voce non può essere nulla");
        }
        for (int i = 0; i < elenco.size(); i++) {
            if (elenco.get(i).getIdVoce().equals(vAggiornata.getIdVoce())) {
                elenco.set(i, vAggiornata);
                ricostruisciReparti();
                return;
            }
        }
        throw new NoSuchElementException("Nessuna voce trovata con id: " + vAggiornata.getIdVoce());
    }

    /**
     * Elimina una voce tramite il suo idVoce,
     * poi ricostruisce la HashMap dei reparti.
     *
     * @param idVoce l'id della voce da eliminare
     * @throws NoSuchElementException se l'id non viene trovato
     */
    public void eliminaVoce(String idVoce) {
        if (idVoce == null || idVoce.trim().isEmpty()) {
            throw new IllegalArgumentException("Id non valido");
        }
        boolean rimossa = elenco.removeIf(v -> v.getIdVoce().equals(idVoce));
        if (!rimossa) {
            throw new NoSuchElementException("Nessuna voce trovata con id: " + idVoce);
        }
        ricostruisciReparti();
    }

    /**
     * Ricostruisce da zero la HashMap dei reparti a partire dall'elenco.
     * Viene chiamata dopo ogni modifica o eliminazione.
     */
    private void ricostruisciReparti() {
        reparti.clear();
        for (VoceBudget v : elenco) {
            aggiungiAReparti(v);
        }
    }

    // =========================================================
    // RICERCHE (requisito UDA: almeno 2 campi)
    // =========================================================

    /**
     * Restituisce tutte le voci il cui nomeReparto contiene la stringa cercata
     * (ricerca case-insensitive).
     */
    public ArrayList<VoceBudget> cercaPerReparto(String query) {
        ArrayList<VoceBudget> risultati = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return risultati;

        for (VoceBudget v : elenco) {
            if (v.getNomeReparto().toLowerCase().contains(query.trim().toLowerCase())) {
                risultati.add(v);
            }
        }
        return risultati;
    }

    /**
     * Restituisce tutte le voci che appartengono a una categoria specifica
     * (ricerca case-insensitive).
     */
    public ArrayList<VoceBudget> cercaPerCategoria(String categoria) {
        ArrayList<VoceBudget> risultati = new ArrayList<>();
        if (categoria == null || categoria.trim().isEmpty()) return risultati;

        for (VoceBudget v : elenco) {
            if (v.getCategoria().toLowerCase().contains(categoria.trim().toLowerCase())) {
                risultati.add(v);
            }
        }
        return risultati; 
    }
    
    
    public ArrayList<VoceBudget> cercaPerId(String id) {
        ArrayList<VoceBudget> risultati = new ArrayList<>();
        if (id == null || id.trim().isEmpty()) return risultati;

        for (VoceBudget v : elenco) {
            if (v.getIdVoce().equalsIgnoreCase(id.trim())) {
                risultati.add(v);
            }
        }
        return risultati;
    }

    public ArrayList<VoceBudget> cercaPerData(String dataInput) {
        ArrayList<VoceBudget> risultati = new ArrayList<>();
        if (dataInput == null || dataInput.trim().isEmpty()) return risultati;

        for (VoceBudget v : elenco) {
            if (v.getDataFormatted().equalsIgnoreCase(dataInput.trim())) {
                risultati.add(v);
            }
        }
         return risultati;
    }

    public ArrayList<VoceBudget> cercaPerDescrizione(String testo) {
        ArrayList<VoceBudget> risultati = new ArrayList<>();
         if (testo == null || testo.trim().isEmpty()) return risultati;

        String q = testo.trim().toLowerCase();
        for (VoceBudget v : elenco) {
            if (v.getDescrizione() != null && v.getDescrizione().toLowerCase().contains(q)) {
                risultati.add(v);
            }
        }
        return risultati;
    }
    
    
        
    // =========================================================
    // GETTER GENERALI (usati dalla GUI)
    // =========================================================

    public ArrayList<VoceBudget> getElenco() {
        return elenco;
    }

    public HashMap<String, Reparto> getReparti() {
        return reparti;
    }

    public Reparto getReparto(String nomeReparto) {
        return reparti.get(nomeReparto);
    }

    /**
     * Genera un nuovo id univoco a 4 cifre basandosi sul massimo id presente.
     * Utile nel dialog di inserimento per pre-compilare il campo id.
     */
    public String generaNuovoId() {
        int max = 0;
        for (VoceBudget v : elenco) {
            try {
                int id = Integer.parseInt(v.getIdVoce());
                if (id > max) max = id;
            } catch (NumberFormatException e) {
                // ignora id malformati
            }
        }
        return String.format("%04d", max + 1);
    }
    
    
    
}