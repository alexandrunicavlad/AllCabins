package com.alexandrunica.allcabins.profile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.profile.adapter.SpinnerAdapter;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Spinner currency = findViewById(R.id.spinner_currency);
        final Spinner language = findViewById(R.id.spinner_language);

        SpinnerAdapter currencyAdapter = new SpinnerAdapter(this,new String[]{"RON","USD","EUR","GBP"});
        final SpinnerAdapter languageAdapter = new SpinnerAdapter(this,new String[]{"Romanian","English"});

        currency.setAdapter(currencyAdapter);
        language.setAdapter(languageAdapter);

        ImageView back = findViewById(R.id.setting_back);
        ImageView apply = findViewById(R.id.setting_apply);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String languageString =((TextView) (language.getSelectedView().findViewById(R.id.text))).getText().toString();
                String currencyString =((TextView) (currency.getSelectedView().findViewById(R.id.text))).getText().toString();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this   );
                SharedPreferences.Editor editor = sharedPref.edit();
                if (languageString.equals("English")) {
                    setLocale("en");
                    editor.putString("language", "en");
                } else {
                    setLocale("ro");
                    editor.putString("language", "ro");
                }

                editor.putString("currency", currencyString.toUpperCase());
                editor.apply();
                finish();
            }
        });

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String currencyFromDb = preferences.getString("currency", "");
        final String languageFromDb = preferences.getString("language", "");
        currency.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currencyFromDb != null && !currencyFromDb.equals("")) {
                    switch (currencyFromDb) {
                        case "RON":
                            currency.setSelection(0, true);
                            break;
                        case "USD":
                            currency.setSelection(1, true);
                            break;
                        case "EUR":
                            currency.setSelection(2, true);
                            break;
                        case "GBP":
                            currency.setSelection(3, true);
                            break;
                        default:
                            currency.setSelection(1, true);

                    }
                }
            }
        }, 100);

        language.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (languageFromDb != null && !languageFromDb.equals("")) {
                    switch (languageFromDb) {
                        case "ro":
                            language.setSelection(0, true);
                            break;
                        case "en":
                            language.setSelection(1, true);
                            break;
                        default:
                            language.setSelection(1, true);

                    }
                }
            }
        }, 100);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
