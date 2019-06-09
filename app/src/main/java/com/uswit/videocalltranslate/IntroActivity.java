package com.uswit.videocalltranslate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import java.util.Objects;

public class IntroActivity extends AppCompatActivity {

    AlphaAnimation anim;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_intro);

        RelativeLayout view = findViewById(R.id.intro_layout);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Layout has happened here.

                        TextView txtVideo = findViewById(R.id.txt_video);
                        TextView txtAmp = findViewById(R.id.txt_amp);
                        TextView txtRealtime = findViewById(R.id.txt_realtime);
                        TextView txtTranslation = findViewById(R.id.txt_translation);
                        TextView txtChatting = findViewById(R.id.txt_chatting);
                        TextView txtVi = findViewById(R.id.txt_vi);
                        TextView txtVi2 = findViewById(R.id.txt_vi2);
                        TextView txtChat = findViewById(R.id.txt_chat);
                        TextView txtChat2 = findViewById(R.id.txt_chat2);
                        TextView txtViChat = findViewById(R.id.txt_vichat);

                        AnimationSet set = new AnimationSet(true);
                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        set.addAnimation(anim);

                        anim = new AlphaAnimation(1f, 0f);
                        anim.setDuration(300);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        txtVideo.startAnimation(set);

                        set = new AnimationSet(true);
                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        anim.setStartOffset(400);
                        set.addAnimation(anim);

                        anim = new AlphaAnimation(1f, 0f);
                        anim.setDuration(300);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        txtAmp.startAnimation(set);

                        set = new AnimationSet(true);
                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        anim.setStartOffset(600);
                        set.addAnimation(anim);

                        anim = new AlphaAnimation(1f, 0f);
                        anim.setDuration(300);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        txtRealtime.startAnimation(set);

                        set = new AnimationSet(true);
                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        anim.setStartOffset(800);
                        set.addAnimation(anim);

                        anim = new AlphaAnimation(1f, 0f);
                        anim.setDuration(300);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        txtTranslation.startAnimation(set);

                        set = new AnimationSet(true);
                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        anim.setStartOffset(1000);
                        set.addAnimation(anim);

                        anim = new AlphaAnimation(1f, 0f);
                        anim.setDuration(300);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        txtChatting.startAnimation(set);

                        set = new AnimationSet(true);

                        anim = new AlphaAnimation(0f, 1f);
                        anim.setDuration(200);
                        anim.setStartOffset(1200);
                        set.addAnimation(anim);

                        TranslateAnimation animation = new TranslateAnimation(0, txtVi2.getX()-txtVi.getX(),0 , txtVi2.getY()-txtVi.getY());
                        animation.setDuration(600);
                        animation.setStartOffset(1400);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                txtViChat.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        set.addAnimation(animation);

                        txtVi.startAnimation(set);

                        animation = new TranslateAnimation(0, txtChat2.getX()-txtChat.getX(),0 , txtChat2.getY()-txtChat.getY());
                        animation.setDuration(600);
                        animation.setStartOffset(1400);

                        set = new AnimationSet(true);
                        set.addAnimation(anim);
                        set.addAnimation(animation);

                        txtChat.startAnimation(set);

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(IntroActivity.this, txtViChat, Objects.requireNonNull(ViewCompat.getTransitionName(txtViChat)));
                            startActivity(intent, options.toBundle());
                            overridePendingTransition(R.anim.fade, R.anim.hold);
                        }, 2000);

                        // Don't forget to remove your listener when you are done with it.
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();

        supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        // Nope Back
    }
}
