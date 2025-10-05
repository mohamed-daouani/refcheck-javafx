package utils;

/**
 * Représente une fiche contenant les informations extraites
 * d'une carte d'identité (OCR ou MRZ).
 */
public class CarteInfo {
    private final String nom;
    private final String prenom;
    private final String dateNaissance;
    private final String nationalite;
    private final String sexe;
    private final String dateExpiration;
    private final String numeroRegistre;
    private final String numeroCarte;
    private int equipeId = -1; // -1 par défaut, à setter après création

    /**
     * Constructeur utilisé pour les cartes extraites depuis la MRZ.
     */
    public CarteInfo(String nom, String prenom, String dateNaissance, String nationalite,
                     String sexe, String dateExpiration, String numeroRegistre, String numeroCarte) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.sexe = sexe;
        this.dateExpiration = dateExpiration;
        this.numeroRegistre = numeroRegistre;
        this.numeroCarte = numeroCarte;
    }

    /**
     * Constructeur alternatif simplifié pour les cas d'extraction par regex (sans MRZ).
     */
    public CarteInfo(String nom, String prenom, String dateNaissance, String adresse, int age) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = "";
        this.sexe = "";
        this.dateExpiration = "";
        this.numeroRegistre = adresse; // temporairement utilisé pour adresse
        this.numeroCarte = String.valueOf(age); // temporairement utilisé pour âge
    }

    @Override
    public String toString() {
        return "Nom : " + nom + "\n" +
                "Prénom : " + prenom + "\n" +
                "Sexe : " + sexe + "\n" +
                "Date de naissance : " + dateNaissance + "\n" +
                "Nationalité : " + nationalite + "\n" +
                "Date d'expiration : " + dateExpiration + "\n" +
                "N° registre : " + numeroRegistre + "\n" +
                "N° carte : " + numeroCarte;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public String getNationalite() {
        return nationalite;
    }

    public String getSexe() {
        return sexe;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public String getNumeroRegistre() {
        return numeroRegistre;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public int getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(int equipeId) {
        this.equipeId = equipeId;
    }
}

