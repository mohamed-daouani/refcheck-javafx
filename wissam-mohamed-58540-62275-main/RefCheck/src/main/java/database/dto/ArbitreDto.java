package database.dto;

import model.Role;

import java.time.LocalDate;

public record ArbitreDto(int matricule,
                         String nom,
                         String prenom,
                         Role role,
                         LocalDate dateNaissance,
                         LocalDate dateExpiration,
                         boolean suspension,
                         String login,
                         String face_url) {

}
