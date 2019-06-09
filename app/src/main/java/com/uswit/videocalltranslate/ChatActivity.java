package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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


    private String fileDir;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();

        String recentName = intent.getStringExtra("recentName");
        String roomName = intent.getStringExtra("roomName");
        String date = intent.getStringExtra("date");
        fileDir = intent.getStringExtra("fileDir");

        TextView barTitle = findViewById(R.id.txt_room);
        barTitle.setText(roomName);
        TextView barContent = findViewById(R.id.txt_content);
        barContent.setText(recentName);
        TextView barSubTitle = findViewById(R.id.barSubTitle);
        barSubTitle.setText(date);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

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
        getMenuInflater().inflate(R.menu.chat_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.delete:
                AlertDialog.Builder ad = new AlertDialog.Builder(ChatActivity.this);

                ad.setTitle("Chatting recent delete...");       // 제목 설정
                ad.setMessage("Do you really want to delete this?");   // 내용 설정

                ad.setPositiveButton("Yes", (dialog, which) -> {
                    File f = new File(fileDir);
                    if(f.delete()) {
                        Toast.makeText(ChatActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                        supportFinishAfterTransition();
                    } else {
                        Toast.makeText(ChatActivity.this, "Delete file failed", Toast.LENGTH_SHORT).show();
                    }
                });

                ad.setNegativeButton("No", (dialog, which) -> {

                });

                ad.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
    }
}
