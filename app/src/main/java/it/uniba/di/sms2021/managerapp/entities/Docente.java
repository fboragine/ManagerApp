package it.uniba.di.sms2021.managerapp.entities;

import java.io.Serializable;

public class Docente extends Utente implements Serializable {
    private static final long serialVersionUID = 166L;

    public Docente(){
        super();
    }
    public Docente(String id, String matricola, String nome, String cognome, String email) {
        super(id, matricola, nome, cognome, email);
    }

    public Docente(String id, String email) {
        super(id, email);
    }
}
