package local.wallet.analyzing.model;

import java.util.Date;

/**
 * Created by huynh.thanh.huan on 11/23/2015.
 */
public class Transaction {

    private int id;
    private String name;
    private int amount;
    private int category;
    private Date time;
    private int account;
    private String reason;
    private String event;

    public Transaction() {
    }

    public Transaction(int id, String name, int amount, int category, Date time, int account, String reason, String event) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.account = account;
        this.amount = amount;
        this.category = category;
        this.reason = reason;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
