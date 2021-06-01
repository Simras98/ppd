package com.uparis.ppd.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long arrived;
    private long start;
    private long stop;
    private long delay;
    private boolean delayed;
    private boolean notified;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "subscription", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    public Subscription() {
    }

    public Subscription(long arrived, long start, long stop, long delay, boolean delayed, boolean notified, Member member, Status status, Association association, Set<Transaction> transactions) {
        this.arrived = arrived;
        this.start = start;
        this.stop = stop;
        this.delay = delay;
        this.delayed = delayed;
        this.notified = notified;
        this.member = member;
        this.status = status;
        this.association = association;
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArrived() {
        return arrived;
    }

    public void setArrived(long arrived) {
        this.arrived = arrived;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long end) {
        this.stop = end;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }
}
