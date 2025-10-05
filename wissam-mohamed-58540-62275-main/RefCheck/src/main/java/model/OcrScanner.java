package model;

import javafx.application.Platform;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import database.dto.MatchDto;
import utils.CarteInfo;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.*;

public class OcrScanner {

    private static final ITesseract tesseract = new Tesseract();
    private static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static MatchDto currentMatch;


    /**
     * Sets the current match context to associate scanned data.
     *
     * @param match the MatchDto representing the current match
     */
    public static void setCurrentMatch(MatchDto match) {
        currentMatch = match;
    }


    /**
     * Sets the currently active team ID within the context of the current match.
     *
     * @param equipeId the team ID to set
     */
    public static void setEquipeActuelle(int equipeId) {
        if (currentMatch != null) {
            MatchScans.setEquipeActuelle(currentMatch, equipeId);
        }
    }


    /**
     * Retrieves the currently active team ID for the current match.
     *
     * @return the team ID, or -1 if no match is set
     */
    public static int getEquipeActuelle() {
        if (currentMatch != null) {
            return MatchScans.getEquipeActuelle(currentMatch);
        }
        return -1;
    }

    /**
     * Clears the scans and resets the current match context.
     */
    public static void clearScan() {
        if (currentMatch != null) {
            MatchScans.clearScans(currentMatch);
        }
        currentMatch = null;
    }


    /**
     * Performs OCR extraction on a list of images using multiple threads and adds results
     * to the current match context. Executes the provided callback when finished.
     *
     * @param images   the list of BufferedImage objects to process
     * @param onFinish a Runnable callback to execute upon completion
     */
    public static void extractCarteInfoMultiThread(List<BufferedImage> images, Runnable onFinish) {

        setFileToScan();
        if (images == null || images.isEmpty()) {
            onFinish.run();
            return;
        }

        // Initialiser une liste temporaire pour stocker les résultats avant de les ajouter à MatchScans
        final List<CarteInfo> extractedCards = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(images.size());

        for (BufferedImage image : images) {
            executor.submit(() -> {
                try {
                    if (image == null || image.getWidth() == 0 || image.getHeight() == 0) {
                    } else {
                        String text;
                        synchronized (tesseract) {
                            text = tesseract.doOCR(image);
                        }
                        // Supprimer tous les espaces sauf les retours à la ligne
                        text = text.replaceAll("[ \\t\\x0B\\f\\r]+", "");
                        CarteInfo info;

                        try {
                            if (text.trim().split("\\R").length >= 3 && text.contains("<<")) {
                                info = extractInfo(text);
                            } else {
                                String nom = extractByRegex(text, "Nom\\s*[:|-]?\\s*(.+)");
                                String prenom = extractByRegex(text, "Pr[é|e]nom\\s*[:|-]?\\s*(.+)");
                                String dateNaissance = extractByRegex(text, "Naissance\\s*[:|-]?\\s*(\\d{2}/\\d{2}/\\d{4})");
                                String adresse = extractByRegex(text, "Adresse\\s*[:|-]?\\s*(.+)");
                                int age = calculateAgeFromDate(dateNaissance);
                                info = new CarteInfo(nom, prenom, dateNaissance, adresse, age);
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'extraction des informations : " + e.getMessage());
                            // Créer une carte avec des informations minimales pour indiquer l'erreur
                            info = new CarteInfo("Erreur", "Erreur", "Erreur", "Erreur", "Erreur", "Erreur", "Erreur", "Erreur");
                        }

                        // Associer l'équipe courante à la carte
                        info.setEquipeId(getEquipeActuelle());

                        synchronized (extractedCards) {
                            extractedCards.add(info);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur OCR : " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Nouvelle tâche pour attendre la fin de tout
        new Thread(() -> {
            try {
                latch.await(); // Attend que toutes les images soient traitées

                // Une fois toutes les cartes traitées, les ajouter au match
                if (currentMatch != null) {
                    List<CarteInfo> existing = MatchScans.getScans(currentMatch);
                    for (CarteInfo card : extractedCards) {
                        boolean dejaExistant = existing.stream()
                                .anyMatch(c -> c.getNumeroRegistre().equals(card.getNumeroRegistre()));
                        if (!dejaExistant) {
                            MatchScans.ajouterScan(currentMatch, card);
                        } else {
                        }
                    }
                } else {
                    System.out.println("ERREUR: Impossible d'ajouter les cartes, match courant null");
                }

                Platform.runLater(onFinish); // On revient sur le thread JavaFX
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Configures the Tesseract engine with the correct data path and language.
     */
    public static void setFileToScan() {
        String dataPath = getTrainedDataDirectory().toString();
        System.setProperty("TESSDATA_PREFIX", dataPath);
        String dataFile = Paths.get(getTrainedDataDirectory()).toFile().getPath();
        tesseract.setLanguage("fra");
        tesseract.setDatapath(dataFile);
    }

    /**
     * Retrieves the URI to the trained data directory used by Tesseract.
     *
     * @return the URI pointing to the tessdata directory
     * @throws RuntimeException if the resource cannot be located
     */
    public static URI getTrainedDataDirectory() {
        try {
            return Objects.requireNonNull(OcrScanner.class.getClassLoader().getResource("data")).toURI();
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException("Impossible de localiser le dossier tessdata", e);
        }
    }

    /**
     * Extracts structured information from a block of text formatted as MRZ (Machine Readable Zone).
     *
     * @param text the text content to parse
     * @return a CarteInfo object containing the parsed data
     * @throws IllegalArgumentException if the format is invalid
     */
    public static CarteInfo extractInfo(String text) {
        String[] lines = text.split("\\R");
        if (lines.length < 3) {
            throw new IllegalArgumentException("Format invalide : 3 lignes requises.");
        }

        String line1 = lines[0].trim();
        String line2 = lines[1].trim();
        String line3 = lines[2].trim();


        String numeroCarte = line1.substring(5, 14) + line1.substring(15, 18);

        String dateNaissanceRaw = line2.substring(0, 6);
        String sexe = line2.substring(7, 8);
        String dateExpirationRaw = line2.substring(8, 15);
        String nationalite = line2.substring(15, 18);
        String numRegistre = line2.substring(18, 29);


        String dateNaissance = formatDate(dateNaissanceRaw);
        String dateExpiration = formatDate(dateExpirationRaw);

        String nom = "Inconnu";
        String prenom = "Inconnu";

        try {
            String[] nomPrenom = line3.split("<<");
            if (nomPrenom.length >= 1) {
                nom = nomPrenom[0].replace("<", " ").trim();
            }
            if (nomPrenom.length >= 2) {
                prenom = nomPrenom[1].replace("<", " ").trim();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction nom/prénom : " + e.getMessage());
        }

        return new CarteInfo(nom, prenom, dateNaissance, nationalite, sexe, dateExpiration, numRegistre, numeroCarte);
    }

    /**
     * Helper method to reformat a raw date string (YYMMDD) into YYYY-MM-DD format.
     *
     * @param rawDate the raw date string
     * @return the formatted date string
     */
    private static String formatDate(String rawDate) {
        String yy = rawDate.substring(0, 2).replaceAll("\\D", "");
        String mm = rawDate.substring(2, 4).replaceAll("\\D", "");
        String dd = rawDate.substring(4, 6).replaceAll("\\D", "");

        int year = 2000 + (yy.isEmpty() ? 0 : Integer.parseInt(yy));
        int month = mm.isEmpty() ? 1 : Integer.parseInt(mm);
        int day = dd.isEmpty() ? 1 : Integer.parseInt(dd);

        return String.format("%d-%02d-%02d", year, month, day);
    }

    /**
     * Retrieves the list of extracted CarteInfo objects for the current match.
     *
     * @return a list of CarteInfo, or an empty list if no match is set
     */
    public static List<CarteInfo> getExtractedInfos() {
        if (currentMatch != null) {
            List<CarteInfo> scans = MatchScans.getScans(currentMatch);
            return scans;
        }
        return new ArrayList<>();
    }

    /**
     * Helper method to extract a value from a text block using a provided regex pattern.
     *
     * @param text  the text to search
     * @param regex the regex pattern to apply
     * @return the extracted value, or "Non trouvé" if not found
     */
    private static String extractByRegex(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Non trouvé";
    }

    /**
     * Calculates the approximate age based on a birthdate string.
     *
     * @param date the birthdate in format dd/MM/yyyy
     * @return the calculated age, or 0 if parsing fails
     */
    static int calculateAgeFromDate(String date) {
        try {
            LocalDate birthDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return LocalDate.now().getYear() - birthDate.getYear();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

}
