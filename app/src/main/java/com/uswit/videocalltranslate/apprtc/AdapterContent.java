package com.uswit.videocalltranslate.apprtc;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.uswit.videocalltranslate.R;
import com.uswit.videocalltranslate.Translate;

public class AdapterContent {
    String msg;
    int type;

    String lang;

    String transText;

    String originalText;


    TextView msgBox;

    boolean transOn = false;


    public static class Builder {
        String msg;
        int type;

        String lang = "ko-KR";

        String transText = null;

        public Builder(String msg, int type) {
            this.msg = msg;
            this.type = type;
        }

        Builder setLang(String lang) {
            this.lang = lang;
            return this;
        }

        Builder setTransText(String transText) {
            this.transText = transText;
            return this;
        }

        public AdapterContent build() {
            return new AdapterContent(this);
        }
    }

    AdapterContent(Builder builder) {
        msg = builder.msg;
        type = builder.type;
        lang = builder.lang;
        transText = builder.transText;
    }

    public AdapterContent get() {
        originalText = msg;

        switch (type) {
            case R.id.chat_local:
                Translate translate = new Translate(handler);
                translate.run(msg, lang);
                break;

            case R.id.chat_remote:
                break;
        }

        return this;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            super.handleMessage(message);

            switch (message.what) {
                case R.id.translateResult:
                    transText = message.obj.toString();
                    break;
                default:
            }
        }
    };
}
