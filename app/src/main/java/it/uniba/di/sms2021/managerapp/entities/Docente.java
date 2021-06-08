package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Docente extends Utente implements Serializable, Parcelable {
    private static final long serialVersionUID = 166L;

    public Docente(){
        super();
    }
    public Docente(String id, String matricola, String nome, String cognome, String email) {
        super(id, matricola, nome, cognome, email);
    }

    public Docente(String id, String email) {
        super(id, email);
    }

    public static final Creator<Utente> CREATOR = new Creator<Utente>() {
        @Override
        public Docente createFromParcel(Parcel in) {
            return new Docente(in);
        }

        @Override
        public Docente[] newArray(int size) {
            return new Docente[size];
        }
    };

    public Docente(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
