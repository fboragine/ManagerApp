package entities;

import java.io.Serializable;

public class Studente extends Utente implements Serializable {
    private static final long serialVersionUID = 166L;

    private String cDs;

    public Studente() {
        super();
    }

    public Studente(String matricola, String nome, String cognome, String email, String cDs) {
        super (matricola, nome, cognome, email);
        this.cDs = cDs;
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }
}