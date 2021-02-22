package entities;

public class Recensione {
    private int voto;
    private String commento, codiceProgetto, id;

    public Recensione(int voto, String commento, String codiceProgetto, String id) {
        this.voto = voto;
        this.commento = commento;
        this.codiceProgetto = codiceProgetto;
        this.id = id;
    }

    public int getVoto() {
        return voto;
    }

    public void setVoto(int voto) {
        this.voto = voto;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getCodiceProgetto() {
        return codiceProgetto;
    }

    public void setCodiceProgetto(String codiceProgetto) {
        this.codiceProgetto = codiceProgetto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
