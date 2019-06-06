package com.uswit.videocalltranslate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.uswit.videocalltranslate.apprtc.AdapterContent;
import com.uswit.videocalltranslate.apprtc.CustomAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";

    View rootLayout;

    private int revealX;
    private int revealY;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        String recentName = intent.getStringExtra("recentName");
        String roomName = intent.getStringExtra("roomName");
        String date = intent.getStringExtra("date");
        String fileDir = intent.getStringExtra("fileDir");

        rootLayout = findViewById(R.id.chat_layout);

        if (savedInstanceState == null && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        TextView barTitle = findViewById(R.id.barTitle);
        barTitle.setText(roomName + " > " + recentName);
        TextView barSubTitle = findViewById(R.id.barSubTitle);
        barSubTitle.setText(date);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        StringBuilder data = new StringBuilder();

        File file = new File(fileDir);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert scan != null;
        while(scan.hasNextLine()){
            data.append(scan.nextLine());
        }

        ArrayList<AdapterContent> items = new ArrayList<>();

        int localIndex = data.indexOf("<local>");
        int remoteIndex = data.indexOf("<remote>");

        while (localIndex != -1 || remoteIndex != -1) {
            if (((localIndex < remoteIndex) && localIndex != -1) || remoteIndex == -1) {
                String str = data.substring(0, localIndex);
                data.delete(0, localIndex + "<local>".length());
                items.add(new AdapterContent(str.split("\\|")[0], str.split("\\|")[2], R.id.chat_local, str.split("\\|")[1]));
            } else if (remoteIndex < localIndex || localIndex == -1) {
                String str = data.substring(0, remoteIndex);
                data.delete(0, remoteIndex + "<remote>".length());
                items.add(new AdapterContent(str.split("\\|")[0], str.split("\\|")[2], R.id.chat_remote, str.split("\\|")[1]));
            }

            localIndex = data.indexOf("<local>");
            remoteIndex = data.indexOf("<remote>");
        }

        RecyclerView recyclerView = findViewById(R.id.recordList_Call);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter viewAdapter = new CustomAdapter(this, items);

        recyclerView.setAdapter(viewAdapter);
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(400);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    protected void unRevealActivity() {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0);

        circularReveal.setDuration(400);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(R.anim.not_move_activity,R.anim.rightout_activity);
            }
        });

        circularReveal.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recent_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            unRevealActivity();
            return true;
        }// If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        unRevealActivity();
    }
}
