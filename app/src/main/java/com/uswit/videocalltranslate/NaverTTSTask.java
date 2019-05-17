package com.uswit.videocalltranslate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uswit.videocalltranslate.apprtc.CustomAdapter;

public class NaverTTSTask extends AsyncTask<String[], Void, String> {
    int type;
    String text;

    public NaverTTSTask(int _type){
        type = _type;
    }

    @Override
    protected String doInBackground(String[]... strings) {
        APITTS.main(text, type);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public void setText(String _text){
        text = _text;
    }
}
