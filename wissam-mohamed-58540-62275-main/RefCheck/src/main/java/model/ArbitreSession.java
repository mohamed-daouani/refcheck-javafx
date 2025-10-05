package model;

import database.dto.ArbitreDto;

public class ArbitreSession {
    private static ArbitreSession instance;
    private ArbitreDto arbitreCourant;

    private ArbitreSession() {
    }

    public static ArbitreSession getInstance() {
        if (instance == null) {
            instance = new ArbitreSession();
        }
        return instance;
    }

    public void setArbitreCourant(ArbitreDto arbitre) {
        this.arbitreCourant = arbitre;
    }

    public ArbitreDto getArbitreCourant() {
        return arbitreCourant;
    }

    public void clearSession() {
        this.arbitreCourant = null;
    }

}