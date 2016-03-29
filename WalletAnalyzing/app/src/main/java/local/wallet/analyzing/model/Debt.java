package local.wallet.analyzing.model;

/**
 * Created by huynh.thanh.huan on 3/25/2016.
 */
public class Debt {
    private int id;
    private int categoryId;
    private int transactionId;
    private Double amount;
    private String people;

    public Debt() {
    }

    public Debt(int id, int categoryId, int transactionId, Double amount, String people) {
        this.id             = id;
        this.categoryId     = categoryId;
        this.transactionId  = transactionId;
        this.amount         = amount;
        this.people         = people;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    @Override
    public String toString() {
        return "Debt{" +
                "id = "                 + id +
                ", categoryId = "       + categoryId +
                ", transactionId = "    + transactionId +
                ", amount = "           + amount +
                ", people = '"          + people + '\'' +
                '}';
    }
}
