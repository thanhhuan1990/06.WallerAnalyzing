package local.wallet.analyzing.model;

import java.util.ArrayList;

import local.wallet.analyzing.R;

/**
 * Created by huynh.thanh.huan on 1/5/2016.
 */
public class AccountType {
    private int id;
    private int name;
    private int icon;

    public static ArrayList<AccountType> Accounts = new ArrayList<AccountType>() {{
        add(new AccountType(1, R.string.account_type_cash,              R.drawable.account_type_cash));
        add(new AccountType(2, R.string.account_type_bank_account,      R.drawable.account_type_bank_account));
        add(new AccountType(3, R.string.account_type_credit_card,       R.drawable.account_type_credit_card));
        add(new AccountType(4, R.string.account_type_investment,        R.drawable.account_type_investment));
        add(new AccountType(5, R.string.account_type_saving_deposit,    R.drawable.account_type_saving_deposit));
        add(new AccountType(6, R.string.account_type_other,             R.drawable.account_type_other));
    }};

    public AccountType(int id, int name, int icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public static AccountType getAccountTypeById(int id) {
        for(AccountType type : Accounts) {
            if(id == type.getId()) {
                return type;
            }
        }
        return null;
    }
}
