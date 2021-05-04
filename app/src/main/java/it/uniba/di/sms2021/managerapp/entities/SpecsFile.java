package it.uniba.di.sms2021.managerapp.entities;

public class SpecsFile {
    private String id;
    private String nome;
    private String data;
    private long dimensione;
    String percorso;
    private String formato;



    public SpecsFile(String id, String nome, String data, long dimensione, String formato, String percorso) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.dimensione = dimensione;
        this.formato = formato;
        this.percorso = percorso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDimensione() {
        return dimensione;
    }

    public void setDimensione(long dimensione) {
        this.dimensione = dimensione;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getPercorso() {
        return percorso;
    }

    public void setPercorso(String percorso) {
        this.percorso = percorso;
    }
}
