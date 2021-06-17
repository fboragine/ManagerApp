package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Studente extends Utente implements Serializable, Parcelable {
    private static final long serialVersionUID = 166L;

    private String cDs;

    public Studente() {
        super();
    }

    public Studente(String id, String matricola, String nome, String cognome, String email, String cDs) {
        super (id, matricola, nome, cognome, email);
        this.cDs = cDs;
    }

    public Studente(String id, String email) {
        super (id, email);
    }

    public static final Creator<Utente> CREATOR = new Creator<Utente>() {
        @Override
        public Studente createFromParcel(Parcel in) {
            return new Studente(in);
        }

        @Override
        public Studente[] newArray(int size) {
            return new Studente[size];
        }
    };

    public Studente(Parcel in) {
        super(in);
        cDs = in.readString();
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(cDs);
    }
}