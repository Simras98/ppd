package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "status")
public class Status implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean admin;
    private boolean superAdmin;

    @OneToOne(mappedBy = "status")
    private Subscription subscription;

    public Status() {
    }

    public Status(boolean admin, boolean superAdmin) {
        this.admin = admin;
        this.superAdmin = superAdmin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }
}
