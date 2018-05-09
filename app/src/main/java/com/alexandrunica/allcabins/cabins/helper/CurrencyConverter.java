package com.alexandrunica.allcabins.cabins.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.activities.CabinInfoAcitivty;

public class CurrencyConverter {

    private Context context;

    public CurrencyConverter(Context context) {
        this.context = context;
    }


    public String convertCurrency(String price) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String currency = preferences.getString("currency", "");
        String newPrice;
        if (currency != null && !currency.equals("")) {
            float floatPrice = (Float.parseFloat(price));
            float floatCurrency = (Float.parseFloat(byIdName(currency.toLowerCase())));
            float finalPrice = floatPrice / floatCurrency;
            if (!String.valueOf(finalPrice).contains(".0")) {
                newPrice = String.valueOf(finalPrice);
            } else {
                newPrice = String.valueOf(finalPrice).substring(0, String.valueOf(finalPrice).indexOf("."));
            }
        } else {
            if (!price.contains(".0")) {
                newPrice = price;
            } else {
                newPrice = price.replace(".0", "");
            }

        }
        if (newPrice.contains(".")) {
            newPrice = String.format("%.1f", Float.parseFloat(newPrice));
        }
        return newPrice;
    }

    public String addCurrency() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String currency = preferences.getString("currency", "");
        if (currency != null && !currency.equals("")) {
            return currency;
        } else {
            return "RON";
        }
    }

    public String byIdName(String name) {
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(name, "string", context.getPackageName()));
    }
}
