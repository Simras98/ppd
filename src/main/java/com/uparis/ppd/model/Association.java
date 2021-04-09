package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "association")
public class Association implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private double price1Month;
    private double price3Months;
    private double price12Months;

    @OneToMany(mappedBy = "association")
    private Set<Subscription> subscriptions;

    public Association() {
    }

    public Association(String name, String description, double price1Month, double price3Months, double price12Months) {
        this.name = name;
        this.description = description;
        this.price1Month = price1Month;
        this.price3Months = price3Months;
        this.price12Months = price12Months;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice1Month() {
        return price1Month;
    }

    public void setPrice1Month(double price1Month) {
        this.price1Month = price1Month;
    }

    public double getPrice3Months() {
        return price3Months;
    }

    public void setPrice3Months(double price3Months) {
        this.price3Months = price3Months;
    }

    public double getPrice12Months() {
        return price12Months;
    }

    public void setPrice12Months(double price12Months) {
        this.price12Months = price12Months;
    }
}
