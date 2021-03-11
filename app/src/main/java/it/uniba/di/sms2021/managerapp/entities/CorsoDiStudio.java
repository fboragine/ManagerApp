package it.uniba.di.sms2021.managerapp.entities;

import java.util.ArrayList;

public class CorsoDiStudio {

    private String idCorsoDiStudio;
    private String nome;
    private String descrizione;
    private ArrayList<String> idEsami;

    public CorsoDiStudio() {}

    public CorsoDiStudio(String idCorsoDiStudio, String nome, String descrizione, ArrayList<String> idEsami) {
        this.idCorsoDiStudio = idCorsoDiStudio;
        this.nome = nome;
        this.descrizione = descrizione;
        this.idEsami = idEsami;
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

    public ArrayList<String> getIdEsami() {
        return idEsami;
    }

    public void setIdEsami(ArrayList<String> idEsami) {
        this.idEsami = idEsami;
    }
}
