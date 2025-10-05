package database.dto;

import model.Role;

public class MembreClubDto {
    private final String nom;
    private final String prenom;
    private final String role;
    private final String dateNaissance;
    private final String dateExpiration;
    private final String numRegistre;
    private final int clubId;
    private final int suspension;

    public MembreClubDto(String nom, String prenom, Role role, String dateNaissance, String dateExpiration, String numRegistre, int clubId, int suspension) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role.toString();
        this.dateNaissance = dateNaissance;
        this.dateExpiration = dateExpiration;
        this.numRegistre = numRegistre;
        this.clubId = clubId;
        this.suspension = suspension;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getRole() {
        return role;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public String getNumRegistre() {
        return numRegistre;
    }

    public int getClubId() {
        return clubId;
    }

    public int getSuspension() {
        return suspension;
    }
}
