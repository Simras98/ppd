package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(targetEntity = Member.class)
    private Member member;

    private long dateTransaction;
    private double price;

    public Transaction() {
    }

    public Transaction(Member member, long date, double price) {
        this.member = member;
        this.dateTransaction = date;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public long getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(long dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
