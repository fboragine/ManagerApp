package entities;

public class Segreteria {
    private String email, password, nome, id, congome;

    public Segreteria(String email, String password, String nome, String id, String congome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.id = id;
        this.congome = congome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCongome() {
        return congome;
    }

    public void setCongome(String congome) {
        this.congome = congome;
    }
}
