package local.wallet.analyzing.model;

import android.content.Context;

import java.text.DecimalFormat;

import local.wallet.analyzing.main.Configs;
import local.wallet.analyzing.R;

/**
 * Created by huynh.thanh.huan on 1/5/2016.
 */
public class Currency {

    public enum CurrencyList {
        VND(0),
        USD(1),
        JPY(2);

        private int value;

        private CurrencyList(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }
    public static CurrencyList getCurrencyById(int currencyId) {

        CurrencyList currency;
        switch (currencyId) {
            case 0:
                currency = CurrencyList.VND;
                break;
            case 1:
                currency = CurrencyList.USD;
                break;
            case 2:
                currency = CurrencyList.JPY;
                break;
            default:
                currency = CurrencyList.VND;
                break;
        }

        return currency;
    }

    public static int getDefaultCurrencyIcon(Context context) {
        int strResource = -1;
        Configs mConfigs        = new Configs(context);
        CurrencyList currency = getCurrencyById(mConfigs.getInt(Configs.Key.Currency));
        switch (currency) {
            case VND:
                strResource = R.string.currency_icon_vietnam;
                break;
            case USD:
                strResource = R.string.currency_icon_usd;
                break;
            case JPY:
                strResource = R.string.currency_icon_jpy;
                break;
            default:
                strResource = R.string.currency_icon_vietnam;
                break;
        }

        return strResource;
    }
    public static int getCurrencyIcon(int currencyId) {
        int strResource = -1;

        CurrencyList currency = getCurrencyById(currencyId);
        switch (currency) {
            case VND:
                strResource = R.string.currency_icon_vietnam;
                break;
            case USD:
                strResource = R.string.currency_icon_usd;
                break;
            case JPY:
                strResource = R.string.currency_icon_jpy;
                break;
            default:
                strResource = R.string.currency_icon_vietnam;
                break;
        }

        return strResource;
    }

    public static int getCurrencyName(CurrencyList currency) {
        int strResource = -1;

        switch (currency) {
            case VND:
                strResource = R.string.currency_vnd;
                break;
            case USD:
                strResource = R.string.currency_usd;
                break;
            case JPY:
                strResource = R.string.currency_jpy;
                break;
            default:
                strResource = R.string.currency_vnd;
                break;
        }

        return strResource;
    }

    public static String formatCurrencyDouble(int currencyId, Double amount) {
        String strResource = "";

        DecimalFormat df = new DecimalFormat();

        if(amount.longValue() == amount) {
            df = new DecimalFormat("#,###");
        } else {
            df = new DecimalFormat("##,##0.00");
        }
        switch (getCurrencyById(currencyId)) {
            case VND:
                strResource = df.format(amount);
                break;
            case USD:
                strResource = new DecimalFormat("##,##0.00").format(amount);
                break;
            case JPY:
                strResource = new DecimalFormat("##,##0.00").format(amount);
                break;
            default:
                strResource = new DecimalFormat("##,##0.00").format(amount);
                break;
        }

        return strResource;
    }

    public static String formatCurrency(Context context, int currencyId, Double amount) {
        String strResource = "";

        DecimalFormat df = new DecimalFormat();

        if(amount.longValue() == amount) {
            df = new DecimalFormat("#,### ");
        } else {
            df = new DecimalFormat("##,##0.00 ");
        }
        switch (getCurrencyById(currencyId)) {
            case VND:
                strResource = df.format(amount) + context.getResources().getString(R.string.currency_icon_vietnam);
                break;
            case USD:
                strResource = new DecimalFormat("##,##0.00 " + context.getResources().getString(R.string.currency_icon_usd)).format(amount);
                break;
            case JPY:
                strResource = new DecimalFormat("##,##0.00 " + context.getResources().getString(R.string.currency_icon_jpy)).format(amount);
                break;
            default:
                strResource = new DecimalFormat("##,##0.00 " + context.getResources().getString(R.string.currency_icon_vietnam)).format(amount);
                break;
        }

        return strResource;
    }
}
