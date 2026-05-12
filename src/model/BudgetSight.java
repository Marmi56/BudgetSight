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
 * Main manager class for the BudgetSight application.
 * Single entry point for all data access from the GUI.
 * Handles CSV file reading/writing, maintains the list of budget entries and department HashMap,
 * and exposes all calculation methods needed by the 4 dashboards.
 *
 * @author Computer
 * @since 1.0
 */
public class BudgetSight {

    private ArrayList<VoceBudget> elenco;
    private HashMap<String, Reparto> reparti;

    private static final String SEPARATORE = ",";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // CONSTRUCTOR

    /**
     * Constructs an empty BudgetSight manager.
     * Initializes empty data structures.
     */
    public BudgetSight() {
        this.elenco = new ArrayList<>();
        this.reparti = new HashMap<>();
    }

    // =========================================================
    // CSV — FILE READING AND WRITING
    // =========================================================

    /**
     * Loads budget data from a CSV file into memory.
     * Skips the header row and ignores malformed lines without crashing.
     * Populates both the elenco and reparti data structures.
     *
     * @param file the budget.csv file to read
     * @throws IOException if the file cannot be read
     */
    public void caricaCSV(File file) throws IOException {
        elenco.clear();
        reparti.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            boolean primaRiga = true;

            while ((riga = br.readLine()) != null) {
                if (primaRiga) {
                    // Skip header row
                    primaRiga = false;
                    continue;
                }
                if (riga.trim().isEmpty()) {
                    // Skip empty lines
                    continue;
                }

                try {
                    VoceBudget v = parseRiga(riga);
                    elenco.add(v);
                    aggiungiAReparti(v);
                } catch (IllegalArgumentException e) {
                    // Malformed line: skip without crashing (exception-safe)
                    System.err.println("Skipped line: " + riga + " — " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error opening file");
        }
    }

    /**
     * Validates a file path string for CSV output.
     * If the path is invalid, defaults to "outputFile.csv".
     * Handles both file paths and directory paths intelligently.
     *
     * @param percorsoInput the path to validate
     * @return the validated path string
     */
    public static String validaPercorso(String percorsoInput) {
        if (percorsoInput == null || percorsoInput.trim().isEmpty()) {
            return "outputFile.csv";
        }

        percorsoInput = percorsoInput.trim();

        // If ends with slash/backslash -> it's a directory
        if (percorsoInput.endsWith("/") || percorsoInput.endsWith("\\")) {
            return percorsoInput + "outputFile.csv";
        }

        Path path = Paths.get(percorsoInput);
        Path fileName = path.getFileName();

        if (fileName == null) {
            return percorsoInput + "\\outputFile.csv";
        }

        String nomeFinale = fileName.toString();

        // If no .csv extension -> assume it's a directory
        if (!nomeFinale.toLowerCase().endsWith(".csv")) {
            return percorsoInput + "\\outputFile.csv";
        }

        // Path already valid
        return percorsoInput;
    }

    /**
     * Saves all budget entries to a CSV file, overwriting existing content.
     * Includes header row and all data.
     *
     * @param file the destination file
     * @throws IOException if the file cannot be written
     * @throws FileException if the data structure is empty
     */
    public void salvaCSV(File file) throws IOException, FileException {
        if (this.elenco.isEmpty()) {
            throw new FileException("Cannot write file, data structure is empty.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Header row
            bw.write("idVoce,nomeReparto,responsabile,budgetAnnuale,data,categoria,descrizione,importo");
            bw.newLine();

            for (VoceBudget v : elenco) {
                bw.write(v.toCSV());
                bw.newLine();
            }
        }
    }

    /**
     * Saves all budget entries to a specified file path.
     * Validates the path and creates the file if needed.
     *
     * @param path the destination path
     * @throws IOException if the file cannot be written
     * @throws FileException if the data structure is empty or the file cannot be created
     */
    public void salvaCSV(String path) throws FileException, IOException {
        if (this.elenco.isEmpty()) {
            throw new FileException("Cannot write file, data structure is empty.");
        }
        // Path validation
        path = validaPercorso(path);

        File file = new File(path);
        if (!file.createNewFile()) {
            throw new FileException("Cannot create file, check the path: file may already exist.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Header row
            bw.write("idVoce,nomeReparto,responsabile,budgetAnnuale,data,categoria,descrizione,importo");
            bw.newLine();

            for (VoceBudget v : elenco) {
                bw.write(v.toCSV());
                bw.newLine();
            }
        }
    }

    /**
     * Parses a CSV line into a VoceBudget object.
     * Validates all fields and throws IllegalArgumentException on invalid data.
     *
     * @param riga the CSV line to parse
     * @return a new VoceBudget object
     * @throws IllegalArgumentException if any field is invalid
     */
    private VoceBudget parseRiga(String riga) {
        String[] campi = riga.split(SEPARATORE, -1);
        if (campi.length < 8) {
            throw new IllegalArgumentException("Insufficient number of fields");
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
            throw new IllegalArgumentException("Invalid annual budget: " + campi[3]);
        }

        try {
            data = LocalDate.parse(campi[4].trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date: " + campi[4]);
        }

        try {
            importo = Double.parseDouble(campi[7].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount: " + campi[7]);
        }

        return new VoceBudget(idVoce, nomeReparto, responsabile,
                              budgetAnnuale, data, categoria, descrizione, importo);
    }

    /**
     * Inserts a voice into the department HashMap.
     * Creates the department if it doesn't exist yet.
     *
     * @param v the VoceBudget to insert
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
    // METHODS FOR THE 4 DASHBOARDS
    // =========================================================

    /**
     * Dashboard 1 — Total of all departments' annual budgets.
     *
     * @return the sum of all annual budgets in euros
     */
    public double getBudgetTotale() {
        double totale = 0;
        for (Reparto r : reparti.values()) {
            totale += r.getBudgetAnnuale();
        }
        return totale;
    }

    /**
     * Dashboard 2 — Total amount spent across all departments.
     *
     * @return the sum of all expenses in euros
     */
    public double getSpeseTotali() {
        double totale = 0;
        for (VoceBudget v : elenco) {
            totale += v.getImporto();
        }
        return totale;
    }

    /**
     * Dashboard 3 — List of departments that have exceeded their budget.
     *
     * @return an ArrayList of overspent Reparto objects
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

    /**
     * Calculates the total overspending amount across all departments.
     * Only counts departments that have exceeded their budget.
     *
     * @return the total overspending in euros
     */
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
     * Dashboard 4 — Category with the highest total spending.
     * Sums across all departments.
     *
     * @return the name of the most expensive category
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

    /**
     * Returns the department with the highest total spending.
     *
     * @return the Reparto with maximum expenses, or null if no departments exist
     */
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
    // CRUD — INSERT, MODIFY, DELETE
    // =========================================================

    /**
     * Adds a new expense entry to the list and updates the department HashMap.
     *
     * @param v the VoceBudget to insert (cannot be null)
     * @throws IllegalArgumentException if the voice is null
     */
    public void inserisciVoce(VoceBudget v) {
        if (v == null) {
            throw new IllegalArgumentException("Voice cannot be null");
        }
        elenco.add(v);
        aggiungiAReparti(v);
    }

    /**
     * Replaces an existing entry (same idVoce) with updated values.
     * Rebuilds the department HashMap afterward.
     *
     * @param vAggiornata the voice with updated values (cannot be null)
     * @throws NoSuchElementException if the id is not found
     */
    public void modificaVoce(VoceBudget vAggiornata) {
        if (vAggiornata == null) {
            throw new IllegalArgumentException("Voice cannot be null");
        }
        for (int i = 0; i < elenco.size(); i++) {
            if (elenco.get(i).getIdVoce().equals(vAggiornata.getIdVoce())) {
                elenco.set(i, vAggiornata);
                ricostruisciReparti();
                return;
            }
        }
        throw new NoSuchElementException("No voice found with id: " + vAggiornata.getIdVoce());
    }

    /**
     * Deletes an entry by its voice ID.
     * Rebuilds the department HashMap afterward.
     *
     * @param idVoce the ID of the voice to delete (cannot be null or empty)
     * @throws IllegalArgumentException if the id is null or empty
     * @throws NoSuchElementException if the id is not found
     */
    public void eliminaVoce(String idVoce) {
        if (idVoce == null || idVoce.trim().isEmpty()) {
            throw new IllegalArgumentException("ID is invalid");
        }
        boolean rimossa = elenco.removeIf(v -> v.getIdVoce().equals(idVoce));
        if (!rimossa) {
            throw new NoSuchElementException("No voice found with id: " + idVoce);
        }
        ricostruisciReparti();
    }

    /**
     * Rebuilds the department HashMap from scratch using the current elenco.
     * Called after every modification or deletion operation.
     */
    private void ricostruisciReparti() {
        reparti.clear();
        for (VoceBudget v : elenco) {
            aggiungiAReparti(v);
        }
    }

    // =========================================================
    // SEARCH METHODS (requirement: at least 2 fields)
    // =========================================================

    /**
     * Searches for entries whose department name contains the given query string.
     * Search is case-insensitive.
     *
     * @param query the search string
     * @return an ArrayList of matching VoceBudget objects (empty if no matches)
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
     * Searches for entries belonging to a specific category.
     * Search is case-insensitive.
     *
     * @param categoria the category name to search for
     * @return an ArrayList of matching VoceBudget objects (empty if no matches)
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

    /**
     * Searches for an entry by its exact voice ID.
     * Search is case-insensitive.
     *
     * @param id the voice ID to search for
     * @return an ArrayList containing the matching voice, or empty if not found
     */
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

    /**
     * Searches for entries with a specific expense date.
     * Date must be in format "dd-MM-yyyy".
     *
     * @param dataInput the date string to search for
     * @return an ArrayList of matching VoceBudget objects (empty if no matches)
     */
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

    /**
     * Searches for entries whose description contains the given text.
     * Search is case-insensitive.
     *
     * @param testo the text to search for in descriptions
     * @return an ArrayList of matching VoceBudget objects (empty if no matches)
     */
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
    // GENERAL GETTERS (used by the GUI)
    // =========================================================

    /**
     * Returns the complete list of all budget entries.
     *
     * @return the ArrayList of VoceBudget objects
     */
    public ArrayList<VoceBudget> getElenco() {
        return elenco;
    }

    /**
     * Returns the HashMap of all departments.
     *
     * @return the HashMap with department names as keys and Reparto objects as values
     */
    public HashMap<String, Reparto> getReparti() {
        return reparti;
    }

    /**
     * Retrieves a specific department by name.
     *
     * @param nomeReparto the department name to search for
     * @return the Reparto object, or null if not found
     */
    public Reparto getReparto(String nomeReparto) {
        return reparti.get(nomeReparto);
    }

    /**
     * Generates a new unique 4-digit ID for a new entry.
     * Based on the maximum existing ID in the list.
     * Useful for pre-filling the ID field in insert dialogs.
     *
     * @return a new ID as a 4-digit formatted string (e.g., "0001")
     */
    public String generaNuovoId() {
        int max = 0;
        for (VoceBudget v : elenco) {
            try {
                int id = Integer.parseInt(v.getIdVoce());
                if (id > max) max = id;
            } catch (NumberFormatException e) {
                // Ignore malformed IDs
            }
        }
        return String.format("%04d", max + 1);
    }
}
