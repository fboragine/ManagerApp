package it.uniba.di.sms2021.managerapp.entities;

public class CorsoDiStudio {

    private String idCorsoDiStudio;
    private String nome;
    private String descrizione;

    public CorsoDiStudio(String idCorsoDiStudio, String nome, String descrizione) {
        this.idCorsoDiStudio = idCorsoDiStudio;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    public String getIdCorsoDiStudio() {
        return idCorsoDiStudio;
    }

    public void setIdCorsoDiStudio(String idCorsoDiStudio) {
        this.idCorsoDiStudio = idCorsoDiStudio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
