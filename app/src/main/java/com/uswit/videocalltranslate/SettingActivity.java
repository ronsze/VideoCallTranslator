package com.uswit.videocalltranslate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener{

    static Activity activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);

        activity = this;

        Preference delete_all_records =findPreference("delete_all");
        Preference select_language = findPreference("select_language");

        delete_all_records.setOnPreferenceClickListener(this);
        select_language.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals("delete_all")){
            //모든 통화기록 삭제
            deleteDialog();
        }else if(preference.getKey().equals("select_language")){
            Intent lang_Intent = new Intent(SettingActivity.this, IntroActivity.class);
            lang_Intent.putExtra("isSet", true);
            startActivity(lang_Intent);
            finish();
        }
        return false;
    }

    private void deleteDialog(){
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_smr);
        builder.setNegativeButton(R.string.enter,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)MainActivity.context).remove_all_records();
                    }
                });
        builder.setPositiveButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    public void onBackPressed(){
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }
}
