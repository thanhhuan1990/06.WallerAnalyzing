package local.wallet.analyzing.model;

import java.io.Serializable;

/**
 * Created by huynh.thanh.huan on 11/24/2015.
 */
public class Account {

    public interface IAccountCallback extends Serializable {
        void onListAccountUpdated();
    }

    private int id;

    private String name;

    private int typeId;

    private int currencyId;

    private Double initBalance;

    private String description;

    public Account() {}

    public Account(int id, String name, int typeId, int currencyId, Double initBalance, String description) {
        this.id             = id;
        this.name           = name;
        this.typeId         = typeId;
        this.currencyId     = currencyId;
        this.initBalance         = initBalance;
        this.description    = description;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public Double getInitBalance() {
        return initBalance;
    }

    public void setInitBalance(Double initBalance) {
        this.initBalance = initBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
