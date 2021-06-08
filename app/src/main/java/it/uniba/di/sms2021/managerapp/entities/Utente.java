package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public abstract class Utente  implements Serializable, Parcelable {
    private static final long serialVersionUID = 160L;

    private String id;
    private String matricola;
    private String nome;
    private String cognome;
    private String email;

    public Utente() {}

    public Utente(String id, String matricola, String nome, String cognome, String email) {
        this.id = id;
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public Utente(String id, String email) {
        this.id = id;
        this.email = email;
    }

    protected Utente(Parcel in) {
        id = in.readString();
        matricola = in.readString();
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(matricola);
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(email);
    }
}
