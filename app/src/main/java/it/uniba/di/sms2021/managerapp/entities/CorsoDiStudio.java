package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CorsoDiStudio implements Parcelable {

    private String idCorsoDiStudio;
    private String nome;
    private String descrizione;

    public CorsoDiStudio(String idCorsoDiStudio, String nome, String descrizione) {
        this.idCorsoDiStudio = idCorsoDiStudio;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    protected CorsoDiStudio(Parcel in) {
        idCorsoDiStudio = in.readString();
        nome = in.readString();
        descrizione = in.readString();
    }

    public static final Creator<CorsoDiStudio> CREATOR = new Creator<CorsoDiStudio>() {
        @Override
        public CorsoDiStudio createFromParcel(Parcel in) {
            return new CorsoDiStudio(in);
        }

        @Override
        public CorsoDiStudio[] newArray(int size) {
            return new CorsoDiStudio[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idCorsoDiStudio);
        dest.writeString(nome);
        dest.writeString(descrizione);
    }
}
