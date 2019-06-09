package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroSetting extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static Activity activity;

    private boolean back = false;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.intro_setting);

        activity = this;

        ImageView img = findViewById(R.id.setting);

        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

        img.setAnimation(animation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView img = findViewById(R.id.setting);

        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_reverse);

        img.setAnimation(animation);

        new Handler().postDelayed(() -> {
            back = true;
            onBackPressed();
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        if(back) {
            back = false;
            super.onBackPressed();
        }
    }
}
