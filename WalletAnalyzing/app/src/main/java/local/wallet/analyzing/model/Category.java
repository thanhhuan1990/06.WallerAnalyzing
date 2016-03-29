package local.wallet.analyzing.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huynh.thanh.huan on 11/23/2015.
 */
public class Category implements Serializable {

    public enum EnumDebt implements Serializable {
        MORE(-1),
        NONE(0),
        LESS(1);

        private int value;
        EnumDebt(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static EnumDebt getEnumDebt(int value) {
            for (EnumDebt l : EnumDebt.values()) {
                if (l.value == value) return l;
            }
            throw new IllegalArgumentException("EnumDebt not found. Amputated?");
        }
    }

    private int         id;
    private int         parentId;
    private String      name;
    private boolean     expense;
    private EnumDebt    debt;

    public Category() {}

    public Category(int id, int parentId, String name, boolean expense, EnumDebt debt) {
        this.id         = id;
        this.parentId   = parentId;
        this.name       = name;
        this.expense    = expense;
        this.debt       = debt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpense() {
        return expense;
    }

    public void setExpense(boolean expense) {
        this.expense = expense;
    }

    public EnumDebt getDebtType() {
        return debt;
    }

    public void setDebtType(EnumDebt debt) {
        this.debt = debt;
    }

    @Override
    public String toString() {
        return "Category{" + "id = " + id + ", parentId = " + parentId +", name = \'" + name + "\'" + ", expense = " + expense + ", DebtType = " + debt + "}";
    }
}
