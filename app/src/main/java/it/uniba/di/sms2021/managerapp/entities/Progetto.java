package it.uniba.di.sms2021.managerapp.entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class Progetto implements Parcelable {

    private String id;
    private String nome;
    private String descrizione;
    private String codiceEsame;
    private String dataCreazione;
    private ArrayList<String> idStudenti;
    private boolean stato;

    public Progetto(String id, String nome, String descrizione, String codiceEsame, String dataCreazione, ArrayList<String> idStudenti, Boolean stato) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
        this.idStudenti = idStudenti;
        this.stato = stato;
    }

    public Progetto(String nome) {
        this.nome = nome;
        this.codiceEsame = "prova";
    }

    public Progetto(String nome, String codiceEsame, ArrayList<String> idStudenti) {
        this.id = idGenerator();
        this.nome = nome;
        this.codiceEsame = codiceEsame;
        this.idStudenti = idStudenti;
        this.stato = true;
    }

    public Progetto(String nome, String descrizione, String codiceEsame,
                    String dataCreazione, ArrayList<String> idStudenti) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
        this.idStudenti = idStudenti;
    }

    protected Progetto(Parcel in) {
        id = in.readString();
        nome = in.readString();
        descrizione = in.readString();
        codiceEsame = in.readString();
        dataCreazione = in.readString();
        idStudenti = in.createStringArrayList();
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

    public ArrayList<String> getIdStudenti() {
        return idStudenti;
    }

    public void setIdStudenti(ArrayList<String> idStudenti) {
        this.idStudenti = idStudenti;
    }

    public String idGenerator() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference progettRef = rootRef.child("progetti");
        String key = progettRef.push().getKey();

        return key;
    }

    public boolean isClose() {
        boolean risposta = false;
        if(!stato) {
            risposta = true;
        }
        return risposta;
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
        dest.writeStringList(idStudenti);
    }
}