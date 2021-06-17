package it.uniba.di.sms2021.managerapp.entities;

import java.io.Serializable;

public class Studente extends Utente implements Serializable {
    private static final long serialVersionUID = 166L;

    private String cDs;

    public Studente() {
        super();
    }

    public Studente(String id, String matricola, String nome, String cognome, String email, String cDs) {
        super (id, matricola, nome, cognome, email);
        this.cDs = cDs;
    }
    public Studente(String id, String matricola, String nome, String cognome, String email) {
        super (id, matricola, nome, cognome, email);
        this.cDs = cDs;
    }

    public Studente(String id, String email) {
        super (id, email);
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }
}