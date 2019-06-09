package com.uswit.videocalltranslate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageActivity extends AppCompatActivity {
    private String strLanguage;
    private Intent set_Intent;
    private boolean isSet;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);

        isSet = false;

        set_Intent = getIntent();

        if(set_Intent != null){
            isSet = set_Intent.getBooleanExtra("isSet", false);
        }

        Locale systemLocale = getResources().getConfiguration().locale;
        strLanguage = systemLocale.getLanguage();

        SharedPreferences prefs = getSharedPreferences("PrefSets", MODE_PRIVATE);
        boolean setbool = prefs.getBoolean("setbool", false);
        setbool = !isSet;

        if (setbool) {
            String setLang = prefs.getString("lang", strLanguage);

            changeConfigulation(setLang);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        final ToggleButton toggleButton_ko = findViewById(R.id.input_name);
        final ToggleButton toggleButton_en = findViewById(R.id.btn_english);

        if (strLanguage.equals("ko")) {
            toggleButton_ko.setChecked(true);
            toggleButton_ko.setEnabled(false);
        }
        else {
            toggleButton_en.setChecked(true);
            toggleButton_en.setEnabled(false);
        }

        toggleButton_ko.setOnClickListener(v -> {
            if (toggleButton_en.isChecked()) {
                toggleButton_en.setChecked(false);
                toggleButton_en.setEnabled(true);
                toggleButton_ko.setEnabled(false);
            }

            changeConfigulation("ko");

            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.select_lang);

            isChanged = !isChanged;

            Button button = findViewById(R.id.btn_next);
            if(isChanged)
                button.setText(R.string.btn_change);
            else
                button.setText(R.string.cancel);
        });

        toggleButton_en.setOnClickListener(v -> {
            if (toggleButton_ko.isChecked()) {
                toggleButton_ko.setChecked(false);
                toggleButton_ko.setEnabled(true);
                toggleButton_en.setEnabled(false);
            }

            changeConfigulation("en");

            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.select_lang);

            isChanged = !isChanged;

            Button button = findViewById(R.id.btn_next);
            if(isChanged)
                button.setText(R.string.btn_change);
            else
                button.setText(R.string.cancel);
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            SharedPreferences prefs1 = getSharedPreferences("PrefSets", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs1.edit();
            editor.putString("lang", strLanguage);
            editor.putBoolean("setbool", true);
            editor.apply();

            finishAndStart();
        });
    }

    public void changeConfigulation(String code) {
        Locale mLocale = new Locale(code);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getResources().updateConfiguration(config, null);

        strLanguage = code;
    }

    void finishAndStart() {
        Intent intent = new Intent();
        if (isChanged) {
            //Intent intent = new Intent(IntroActivity.this, InputNameActivity.class);
            intent.putExtra("result", "change");
        } else {
            intent.putExtra("result", "change_none");
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onBackPressed(){
        // Nope Back
    }
}
