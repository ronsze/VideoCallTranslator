package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uswit.videocalltranslate.apprtc.AdapterContent;
import com.uswit.videocalltranslate.apprtc.CustomAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatActivity extends AppCompatActivity {

    SelectRecentActivity selectRecentActivity = (SelectRecentActivity)SelectRecentActivity.selectRecentActivity;

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
        actionBar.setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recent_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_close:
                selectRecentActivity.finish();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
