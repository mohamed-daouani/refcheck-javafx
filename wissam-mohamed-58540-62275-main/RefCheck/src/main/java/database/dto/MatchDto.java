package database.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record MatchDto(int id,
                       int clubAId,
                       int clubBId,
                       String lieu,
                       LocalDate date,
                       LocalTime heure,
                       String score,
                       int arbitreMatricule,
                       String clubANom,
                       String clubBNom,
                       String clubALogoUrl,
                       String clubBLogoUrl) {
}
