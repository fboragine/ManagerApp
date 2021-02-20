package entities;

import java.util.Date;

public class Docente {

    private String matricola;
    private String nome;
    private String cognome;
    private Date data;
    private String percorsoImmagine;

    public Docente(String matricola, String nome, String cognome, Date data, String percorsoImmagine) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.data = data;
        this.percorsoImmagine = percorsoImmagine;
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getPercorsoImmagine() {
        return percorsoImmagine;
    }

    public void setPercorsoImmagine(String percorsoImmagine) {
        this.percorsoImmagine = percorsoImmagine;
    }
}
