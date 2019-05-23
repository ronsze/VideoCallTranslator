package com.uswit.videocalltranslate.apprtc;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.uswit.videocalltranslate.Translate;

public class AdapterContent implements Translate.ThreadReceive, Parcelable {
    private static String TAG = "AdapterContent";

    int type;
    String lang;

    String originalText;
    String transText;

    TextView msgBox;

    boolean transOn = false;

    AdapterContent(String msg, int type, String lang) {
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

    public AdapterContent(String originText, String transText, int type, String lang) {
        this.type = type;
        this.lang = lang;

        originalText = originText;
        this.transText = transText;
    }

    protected AdapterContent(Parcel in) {
        type = in.readInt();
        lang = in.readString();
        originalText = in.readString();
        transText = in.readString();
        msgBox = (TextView) in.readValue(getClass().getClassLoader());
    }

    public static final Creator<AdapterContent> CREATOR = new Creator<AdapterContent>() {
        @Override
        public AdapterContent createFromParcel(Parcel in) {
            return new AdapterContent(in);
        }

        @Override
        public AdapterContent[] newArray(int size) {
            return new AdapterContent[size];
        }
    };

    @Override
    public void onTranslateResult(String result) {
        transText = result;
    }

    @Override
    public void onTranslateError(String result) {
        transText = "";
        Log.e(TAG, result);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(lang);
        dest.writeString(originalText);
        dest.writeString(transText);
        dest.writeValue(msgBox);
    }
}
