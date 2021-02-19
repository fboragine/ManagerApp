package entities;

import java.util.Date;

public class Progetto {
    private String id, nome, descrizione, percorsoImg, codiceEsame;
    private Date dataCreazione;
    private String idStudenti [];
    public static final int MAX_STUDENTI = 5;

    public Progetto(String id, String nome, String descrizione, String percorsoImg,
                    String codiceEsame, Date dataCreazione, String[] idStudenti) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.percorsoImg = percorsoImg;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
        this.idStudenti = new String[MAX_STUDENTI];
        this.idStudenti = idStudenti;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPercorsoImg() {
        return percorsoImg;
    }

    public void setPercorsoImg(String percorsoImg) {
        this.percorsoImg = percorsoImg;
    }

    public String getCodiceEsame() {
        return codiceEsame;
    }

    public void setCodiceEsame(String codiceEsame) {
        this.codiceEsame = codiceEsame;
    }

    public Date getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String[] getIdStudenti() {
        return idStudenti;
    }

    public void setIdStudenti(String[] idStudenti) {
        this.idStudenti = idStudenti;
    }
}
