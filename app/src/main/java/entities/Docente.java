package entities;

import java.io.Serializable;
import java.util.Date;

public class Docente extends Utente implements Serializable {
    private static final long serialVersionUID = 166L;

    public Docente(){
        super();
    }
    public Docente(String matricola, String nome, String cognome, String email) {
        super(matricola, nome, cognome, email);
    }

}
