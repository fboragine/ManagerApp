package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Esame implements Parcelable {

    private String id;
    private String nome;
    private String commento;
    private String descrizione;
    private String cDs;
    private ArrayList<String> idDocenti;

    public Esame(String id, String nome, String commento, String descrizione, String cDs, ArrayList<String> idDocenti) {
        this.id = id;
        this.nome = nome;
        this.commento = commento;
        this.descrizione = descrizione;
        this.cDs = cDs;
        this.idDocenti = idDocenti;
    }

    protected Esame(Parcel in) {
        id = in.readString();
        nome = in.readString();
        commento = in.readString();
        descrizione = in.readString();
        cDs = in.readString();
        idDocenti = in.createStringArrayList();
    }

    public static final Creator<Esame> CREATOR = new Creator<Esame>() {
        @Override
        public Esame createFromParcel(Parcel in) {
            return new Esame(in);
        }

        @Override
        public Esame[] newArray(int size) {
            return new Esame[size];
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

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }

    public ArrayList<String> getIdDocenti() {
        return idDocenti;
    }

    public void setIdDocenti(ArrayList<String> idDocenti) {
        this.idDocenti = idDocenti;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nome);
        dest.writeString(commento);
        dest.writeString(descrizione);
        dest.writeString(cDs);
        dest.writeStringList(idDocenti);
    }
}