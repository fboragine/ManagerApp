package it.uniba.di.sms2021.managerapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class EsameStudente implements Parcelable {

    private String id;
    private String idStudente;
    private String idEsame;
    private boolean stato;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdStudente() {
        return idStudente;
    }

    public void setIdStudente(String idStudente) {
        this.idStudente = idStudente;
    }

    public String getIdEsame() {
        return idEsame;
    }

    public void setIdEsame(String idEsame) {
        this.idEsame = idEsame;
    }

    public boolean getStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public EsameStudente() {}

    public EsameStudente(String id, String idStudente, String idEsame, boolean stato) {
        this.id = id;
        this.idStudente = idStudente;
        this.idEsame = idEsame;
        this.stato = stato;
    }

    protected EsameStudente(Parcel in) {
        id = in.readString();
        idStudente = in.readString();
        idEsame = in.readString();
    }

    public static final Creator<EsameStudente> CREATOR = new Creator<EsameStudente>() {
        @Override
        public EsameStudente createFromParcel(Parcel in) {
            return new EsameStudente(in);
        }

        @Override
        public EsameStudente[] newArray(int size) {
            return new EsameStudente[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(idStudente);
        dest.writeString(idEsame);
    }
}