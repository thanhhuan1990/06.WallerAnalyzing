package local.wallet.analyzing.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by huynh.thanh.huan on 11/23/2015.
 */
public class Transaction implements Comparable<Transaction> {

    private int id;
    private Double amount;
    private String description;
    private int categoryId;
    private int accountId;
    private Calendar time;
    private String payee;
    private String event;

    public Transaction() {
    }

    public Transaction(int id, Double amount, int categoryId, String description, int accountId, Calendar time, String payee, String event) {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
        this.accountId = accountId;
        this.time = time;
        this.payee = payee;
        this.event = event;
    }

    @Override
    public int compareTo(Transaction o) {
        return o.getTime().compareTo(getTime());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
