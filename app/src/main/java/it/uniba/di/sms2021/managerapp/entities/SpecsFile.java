package it.uniba.di.sms2021.managerapp.entities;

public class SpecsFile {
    private String id;
    private String nome;
    private String data;
    private long dimensione;
    private String formato;

    public SpecsFile(String id, String nome, String data, long dimensione, String formato) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.dimensione = dimensione;
        this.formato = formato;
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

}
