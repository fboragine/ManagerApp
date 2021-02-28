package entities;

public class Studente {

    private String id;
    private String matricola;
    private String nome;
    private String cognome;
    private String percorsoImg;
    private String email;
    private String cDs;

    public Studente(String matricola, String nome, String cognome, String email, String cDs) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.percorsoImg = null;
        this.email = email;
        this.cDs = cDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPercorsoImg() {
        return percorsoImg;
    }

    public void setPercorsoImg(String percorsoImg) {
        this.percorsoImg = percorsoImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getcDs() {
        return cDs;
    }

    public void setcDs(String cDs) {
        this.cDs = cDs;
    }

}
