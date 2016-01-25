package local.wallet.analyzing.model;

import java.util.ArrayList;

import local.wallet.analyzing.R;

/**
 * Created by huynh.thanh.huan on 1/5/2016.
 */
public class Currency {
    private int id;
    private int name;
    private int icon;

    public static ArrayList<Currency> Currencies = new ArrayList<Currency>() {{
        add(new Currency(1, R.string.currency_vnd,              R.drawable.currency_vnd));
        add(new Currency(2, R.string.currency_usd,      R.drawable.currency_usd));
        add(new Currency(3, R.string.currency_jpy,              R.drawable.currency_jpy));
    }};

    public Currency(int id, int name, int icon) {
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

    public static Currency getCurrencyById(int id) {
        for(Currency type : Currencies) {
            if(id == type.getId()) {
                return type;
            }
        }
        return null;
    }
}
