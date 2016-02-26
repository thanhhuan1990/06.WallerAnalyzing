package local.wallet.analyzing.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by thanhhuan on 2/19/2016.
 */
public class Budget implements Serializable {
    private int         id;
    private String      name;
    private Double      amount;
    private int[]       categories;
    private int         currency;
    private int         repeatType;
    private Calendar    startDate;
    private Calendar    endDate;
    private boolean     isIncremental;

    public Budget() {
    }

    public Budget(int id, String name, Double amount, int[] categories, int currency, int repeatType, Calendar startDate, Calendar endDate, boolean isIncremental) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.categories = categories;
        this.currency = currency;
        this.repeatType = repeatType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isIncremental = isIncremental;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int[] getCategories() {
        return categories;
    }

    public void setCategories(int[] categories) {
        this.categories = categories;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public boolean isIncremental() {
        return isIncremental;
    }

    public void setIsIncremental(boolean isIncremental) {
        this.isIncremental = isIncremental;
    }

    @Override
    public String toString() {
        return "Budget = (" + id
                            + ", " + name
                            + ", " + amount
                            + ", " + categories.toString()
                            + ", " + currency
                            + ", " + repeatType
                            + ", " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1) + "/" + startDate.get(Calendar.YEAR)
                            + ", " + endDate.get(Calendar.DAY_OF_MONTH) + "/" + (endDate.get(Calendar.MONTH) + 1) + "/" + endDate.get(Calendar.YEAR)
                            + ", " + isIncremental + ")";
    }
}
