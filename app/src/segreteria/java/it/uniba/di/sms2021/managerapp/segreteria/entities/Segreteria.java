package it.uniba.di.sms2021.managerapp.segreteria.entities;

import java.io.Serializable;

public class Segreteria implements Serializable {
    private static final long serialVersionUID = 160L;

    private String id;
    private String email;

    public Segreteria(String id, String email) {
        this.email = email;
        this.id = id;
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
}
