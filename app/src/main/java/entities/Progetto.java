package entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;

public class Progetto implements Parcelable {

    private String id;
    private String nome;
    private String descrizione;
    private String codiceEsame;
    private String dataCreazione;
    private String idStudenti [];
    public static final int MAX_STUDENTI = 5;

    public Progetto(String id, String nome, String descrizione,
                    String codiceEsame, String dataCreazione, String[] idStudenti) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
        this.idStudenti = new String[MAX_STUDENTI];
        this.idStudenti = idStudenti;
    }

    protected Progetto(Parcel in) {
        id = in.readString();
        nome = in.readString();
        descrizione = in.readString();
        codiceEsame = in.readString();
        dataCreazione = in.readString();
        idStudenti = in.createStringArray();
    }

    public static final Creator<Progetto> CREATOR = new Creator<Progetto>() {
        @Override
        public Progetto createFromParcel(Parcel in) {
            return new Progetto(in);
        }

        @Override
        public Progetto[] newArray(int size) {
            return new Progetto[size];
        }
    };

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

    public String getCodiceEsame() {
        return codiceEsame;
    }

    public void setCodiceEsame(String codiceEsame) {
        this.codiceEsame = codiceEsame;
    }

    public String getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public String[] getIdStudenti() {
        return idStudenti;
    }

    public void setIdStudenti(String[] idStudenti) {
        this.idStudenti = idStudenti;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(descrizione);
        dest.writeString(codiceEsame);
        dest.writeString(dataCreazione);
        dest.writeStringArray(idStudenti);
    }
}
