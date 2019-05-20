package com.uswit.videocalltranslate.apprtc;

import android.util.Log;
import android.widget.TextView;

import com.uswit.videocalltranslate.Translate;

public class AdapterContent implements Translate.ThreadReceive {
    private static String TAG = "AdapterContent";

    String msg;
    int type;
    String lang;

    String originalText;
    String transText;

    TextView msgBox;

    boolean transOn = false;

    AdapterContent(String msg, int type, String lang) {
        this.msg = msg;
        this.type = type;
        this.lang = lang;

        originalText = msg;
        Translate translate = new Translate(this, msg, lang);
        translate.start();

        try {
            translate.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onTranslateResult(String result) {
        transText = result;
    }

    @Override
    public void onTranslateError(String result) {
        transText = "";
        Log.e(TAG, result);
    }
}
