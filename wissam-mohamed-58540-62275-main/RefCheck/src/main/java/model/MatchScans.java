package model;

import database.dto.MatchDto;
import utils.CarteInfo;

import java.util.*;

public class MatchScans {
    private static final Map<Integer, List<CarteInfo>> scansParMatch = new HashMap<>();
    private static final Map<Integer, Integer> equipeActuelleParMatch = new HashMap<>();

    /**
     * Adds a scanned card entry to the list associated with a given match.
     *
     * @param match the match to associate the card with
     * @param card  the CarteInfo object representing the scanned card
     */
    public static void ajouterScan(MatchDto match, CarteInfo card) {
        int matchId = match.id();
        if (!scansParMatch.containsKey(matchId)) {
            scansParMatch.put(matchId, new ArrayList<>());
        }
        scansParMatch.get(matchId).add(card);
    }

    /**
     * Retrieves the list of scanned CarteInfo objects for a given match.
     *
     * @param match the match whose scans should be retrieved
     * @return a list of CarteInfo objects, or an empty list if none exist
     */
    public static List<CarteInfo> getScans(MatchDto match) {
        int matchId = match.id();
        List<CarteInfo> scans = scansParMatch.getOrDefault(matchId, new ArrayList<>());
        return scans;
    }

    /**
     * Sets the currently active team ID for a given match.
     *
     * @param match    the match to update
     * @param equipeId the team ID to set as current
     */
    public static void setEquipeActuelle(MatchDto match, int equipeId) {
        equipeActuelleParMatch.put(match.id(), equipeId);
    }

    /**
     * Retrieves the currently active team ID for a given match.
     *
     * @param match the match whose current team ID should be retrieved
     * @return the team ID, or 1 as a default if none is set
     */
    public static int getEquipeActuelle(MatchDto match) {
        return equipeActuelleParMatch.getOrDefault(match.id(), 1);
    }

    /**
     * Clears all scanned data and current team information for a given match.
     *
     * @param match the match whose data should be cleared
     */
    public static void clearScans(MatchDto match) {
        scansParMatch.remove(match.id());
        equipeActuelleParMatch.remove(match.id());
    }

} 