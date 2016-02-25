package local.wallet.analyzing.model;

import java.util.Calendar;

/**
 * Created by thanhhuan on 2/19/2016.
 */
public class Budget {
    private int         id;
    private String      name;
    private Double      amount;
    private int[]       categories;
    private int         currency;
    private int         repeatType;
    private Calendar    fromDate;
    private boolean     isIncremental;

    public Budget() {
    }

    public Budget(int id, String name, Double amount, int[] categories, int currency, int repeatType, Calendar fromDate, boolean isIncremental) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.categories = categories;
        this.currency = currency;
        this.repeatType = repeatType;
        this.fromDate = fromDate;
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

    public Calendar getFromDate() {
        return fromDate;
    }

    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
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
                            + ", " + fromDate.get(Calendar.DAY_OF_MONTH) + "/" + (fromDate.get(Calendar.MONTH) + 1) + "/" +fromDate.get(Calendar.YEAR)
                            + ", " + isIncremental + ")";
    }
}
