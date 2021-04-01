package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "member")
public class Member implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private String password;
    private long startSubscription;
    private long endSubscription;
    private long delaySubscription;
    private boolean delayedSubscription;
    private boolean level;

    public Member() {
    }

    public Member(String firstName,
                  String lastName,
                  String address,
                  String city,
                  String postalCode,
                  String email,
                  String phoneNumber,
                  String password,
                  boolean level) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.delayedSubscription = false;
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public long getDelaySubscription() {
        return delaySubscription;
    }

    public void setDelaySubscription(long delaySubscription) {
        this.delaySubscription = delaySubscription;
    }

    public boolean isDelayedSubscription() {
        return delayedSubscription;
    }

    public void setDelayedSubscription(boolean delayedSubscription) {
        this.delayedSubscription = delayedSubscription;
    }

    public boolean getLevel() {
        return level;
    }

    public void setLevel(boolean level) {
        this.level = level;
    }
}
