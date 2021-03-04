package entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class Progetto {

    private String id;
    private String nome;
    private String descrizione;
    private String codiceEsame;
    private Date dataCreazione;
    private ArrayList<String> idStudenti;

    public Progetto(String id, String nome, String descrizione, String codiceEsame, Date dataCreazione, ArrayList<String> idStudenti) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
        this.idStudenti = idStudenti;
    }

    public Progetto(String nome) {
        this.nome = nome;
    }

    public Progetto(String nome, String codiceEsame, ArrayList<String> idStudenti) {
        this.id = idGenerator();
        this.nome = nome;
        this.codiceEsame = codiceEsame;
        this.idStudenti = idStudenti;
    }

    public Progetto(String nome, String descrizione, String codiceEsame,
                    Date dataCreazione, ArrayList<String> idStudenti) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.codiceEsame = codiceEsame;
        this.dataCreazione = dataCreazione;
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
}