package entities;

import java.util.ArrayList;
import java.util.Date;

public class Esame {

    private String id;
    private String nome;
    private String commento;
    private String cDs;
    private String dataEsame;
    private ArrayList<String> idDocenti;

    public Esame(String id, String nome, String cDs, String dataEsame, ArrayList<String> idDocenti) {
        this.id = id;
        this.nome = nome;
        this.cDs = cDs;
        this.dataEsame = dataEsame;
        this.idDocenti = idDocenti;
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

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }

    public String getDataEsame() {
        return dataEsame;
    }

    public void setDataEsame(String dataEsame) {
        this.dataEsame = dataEsame;
    }
}
