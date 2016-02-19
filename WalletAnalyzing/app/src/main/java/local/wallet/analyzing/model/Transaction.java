package local.wallet.analyzing.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by huynh.thanh.huan on 11/23/2015.
 */
public class Transaction implements Comparable<Transaction>, Serializable {

    public enum TransactionEnum {
        Expense(0),
        Income(1),
        Transfer(2),
        TransferFrom(3),
        TransferTo(4),
        Adjustment(5);

        private int value;
        TransactionEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static TransactionEnum getTransactionEnum(int value) {
            for (TransactionEnum l : TransactionEnum.values()) {
                if (l.value == value) return l;
            }
            throw new IllegalArgumentException("TransactionEnum not found. Amputated?");
        }
    }

    private int id;
    private int transactionType;
    private Double amount;
    private String description;
    private int categoryId;
    private int fromAccountId;
    private int toAccountId;
    private Calendar time;
    private Double fee;
    private String payee;
    private String event;

    public Transaction() {
    }

    public Transaction(int id, int transactionType, Double amount, int categoryId, String description, int fromAccountId, int toAccountId, Calendar time, Double fee, String payee, String event) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
        this.fromAccountId = fromAccountId;
        this.toAccountId    = toAccountId;
        this.time = time;
        this.fee = fee;
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

    public int getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
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

    public int getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(int accountId) {
        this.fromAccountId = accountId;
    }

    public int getToAccountId() {
        return this.toAccountId;
    }

    public void setToAccountId(int accountId) {
        this.toAccountId = accountId;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public Double getFee() {
        return this.fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
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

    @Override
    public String toString() {
        return "Id = " + id + ", TransactionType = " + transactionType + ", amount = " + amount + ", description = \'" + description + "\'";
    }
}
