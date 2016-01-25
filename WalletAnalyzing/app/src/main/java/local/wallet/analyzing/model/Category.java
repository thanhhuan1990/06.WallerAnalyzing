package local.wallet.analyzing.model;

import java.util.List;

/**
 * Created by huynh.thanh.huan on 11/23/2015.
 */
public class Category {

    private int     id;
    private int     parentId;
    private String  name;
    private boolean expense;
    private boolean borrow;
    private List<Category> arCategories;

    public Category() {}

    public Category(int id, int parentId, String name, boolean expense, boolean borrow, List<Category> arCategories) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.expense    = expense;
        this.borrow     = borrow;
        this.arCategories   = arCategories;
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

    public boolean isBorrow() {
        return borrow;
    }

    public void setBorrow(boolean borrow) {
        this.borrow = borrow;
    }

    public List<Category> getArCategories() {
        return arCategories;
    }

    public void setArCategories(List<Category> arCategories) {
        this.arCategories = arCategories;
    }

    @Override
    public String toString() {
        return "Category{" + "id = " + id + ", parentId = " + parentId +", name = \'" + name + "\'" + ", expense = " + expense + ", borrow = " + borrow + "}";
    }
}
