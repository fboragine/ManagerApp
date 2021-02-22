package entities;

import java.util.Date;

public class Esame {

    private String id;
    private String nome;
    private String commento;
    private String cDs;
    private Date dataEsame;

    public Esame(String id, String nome, String commento, String cDs, Date dataEsame) {
        this.id = id;
        this.nome = nome;
        this.commento = commento;
        this.cDs = cDs;
        this.dataEsame = dataEsame;
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

    public Date getDataEsame() {
        return dataEsame;
    }

    public void setDataEsame(Date dataEsame) {
        this.dataEsame = dataEsame;
    }
}
