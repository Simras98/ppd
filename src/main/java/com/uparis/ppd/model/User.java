package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class User implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String address;
  private String postalCode;
  private String city;
  private String phoneNumber;
  private long startSubscription;
  private long endSubscription;

  public User() {}

  public User(
      String firstName,
      String lastName,
      String email,
      String password,
      String address,
      String postalCode,
      String city,
      String phoneNumber) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.address = address;
    this.postalCode = postalCode;
    this.city = city;
    this.phoneNumber = phoneNumber;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public long getStartSubscription() { return this.startSubscription; }

  public void setStartSubscription(long startSubscription) {
    this.startSubscription = startSubscription;
  }

  public long getEndSubscription() {
    return this.endSubscription;
  }

  public void setEndSubscription(long endSubscription) {
    this.endSubscription = endSubscription;
  }
}
