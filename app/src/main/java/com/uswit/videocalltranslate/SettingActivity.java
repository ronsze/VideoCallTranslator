package com.uswit.videocalltranslate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
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
            Intent lang_Intent = new Intent(SettingActivity.this, LanguageActivity.class);
            lang_Intent.putExtra("isSet", true);
            startActivityForResult(lang_Intent, 1);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getStringExtra("result").equals("change")) {
            Intent intent = new Intent();
            intent.putExtra("result", "changed_finish");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("result", "finish");
        setResult(RESULT_OK, intent);
        finish();
    }
}
