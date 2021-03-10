package it.uniba.di.sms2021.managerapp.entities;

import java.io.Serializable;

public abstract class Utente  implements Serializable {
    private static final long serialVersionUID = 160L;

    private String id;
    private String matricola;
    private String nome;
    private String cognome;
    private String email;

    public Utente() {}

    public Utente(String id, String matricola, String nome, String cognome, String email) {
        this.id = id;
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
