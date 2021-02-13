package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne(targetEntity = User.class)
  private User user;

  private long dateTransaction;
  private String label;
  private double price;

  public Transaction() {}

  public Transaction(User user, long date, String label, double price) {
    this.user = user;
    this.dateTransaction = date;
    this.label = label;
    this.price = price;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public long getDateTransaction() {
    return dateTransaction;
  }

  public void setDateTransaction(long dateTransaction) {
    this.dateTransaction = dateTransaction;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }
}
