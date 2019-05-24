package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uswit.videocalltranslate.apprtc.AdapterContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class SelectRecentActivity extends AppCompatActivity {

    private Animation translateLeftAnim;
    private Animation translateRightAnim;
    private Animation translateLeftAnim_sub;
    private Animation translateRightAnim_sub;

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isSub) {
                barTitle.setText(selectedRoomName);

                actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                barTitle.setText("Select Room Name");

                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private ActionBar actionBar;
    Context context;

    private LinearLayout roomLayout;
    private LinearLayout subLayout;
    private RecyclerView recyclerSubView;
    private boolean isSub = false;

    private TextView barTitle;
    private String selectedRoomName;
    private String selectedFileName;
    ArrayList<String> subfName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recent);

        context = this.getApplicationContext();

        barTitle = findViewById(R.id.barTitle);
        barTitle.setText("Select Room Name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerSubView = findViewById(R.id.recycler_view_sub);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        translateLeftAnim_sub = AnimationUtils.loadAnimation(this, R.anim.sub_translate_left);
        translateRightAnim_sub = AnimationUtils.loadAnimation(this, R.anim.sub_translate_right);

        translateLeftAnim_sub.setAnimationListener(animationListener);
        translateRightAnim_sub.setAnimationListener(animationListener);

        roomLayout = findViewById(R.id.roomLayout);
        subLayout = findViewById(R.id.subLayout);

        recyclerView.setHasFixedSize(true);
        recyclerSubView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.LayoutManager subLayoutManager = new LinearLayoutManager(this);
        recyclerSubView.setLayoutManager(subLayoutManager);

        ArrayList<String> fName = new ArrayList<>();
        File files = new File(this.getApplicationContext().getExternalFilesDir(null), "chat");

        String[] textSet = null;

        if (files.exists()) {
            if (files.listFiles().length > 0) {
                for (File file : files.listFiles()) {
                    fName.add(file.getName());
                }

                textSet = fName.toArray(new String[fName.size()]);
                RecyclerView.Adapter adapter = new RecentAdapter(textSet, false);
                recyclerView.setAdapter(adapter);
            } else {
                roomLayout.setVisibility(View.GONE);
                TextView norecent = findViewById(R.id.txt_norecent);
                norecent.setVisibility(View.VISIBLE);
            }
        } else {
            roomLayout.setVisibility(View.GONE);
            TextView norecent = findViewById(R.id.txt_norecent);
            norecent.setVisibility(View.VISIBLE);
        }

        subfName = new ArrayList<>();
        String[] finalTextSet = textSet;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view, int position) {
                if(!isSub) {
                    if(finalTextSet != null) {
                        subfName.clear();
                        ArrayList<String> data = new ArrayList<>();
                        selectedRoomName = finalTextSet[position];
                        File subFiles = new File(files, selectedRoomName);

                        String[] textSet;
                        String prevDate = "";
                        if (subFiles.listFiles().length > 0) {
                            for (File file : subFiles.listFiles()) {
                                subfName.add(file.getName());
                            }

                            subfName.sort((o1, o2) -> {
                                try {
                                    return ((new SimpleDateFormat("yyyyMMdd_HHmmss").parse(o1.substring(0, 15)).getTime()) <= (new SimpleDateFormat("yyyyMMdd_HHmmss").parse(o2.substring(0, 15)).getTime())) ? 1 : -1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            });

                            ArrayList<String> temp = new ArrayList<>(subfName);
                            int index = 0;
                            for(String file : temp) {
                                String date = file.substring(0, 8);
                                try {
                                    if(!date.equals(prevDate)) {
                                        data.add('T' + new SimpleDateFormat("yyyy.MM.dd.").format(new SimpleDateFormat("yyyyMMdd").parse(date)));
                                        prevDate = date;
                                        subfName.add(index, "date");
                                        index++;
                                    }
                                    String name;
                                    if (file.length() > 15) name = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("HHmmss").parse(file.substring(9, 15))) + "_" + file.substring(16);
                                    else name = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("HHmmss").parse(file.substring(9, 15)));
                                    data.add('D' + name);
                                } catch (ParseException e) {
                                    Log.e("SelectRecentActivity", e.toString());
                                }
                                index++;
                            }

                            textSet = data.toArray(new String[data.size()]);
                            RecyclerView.Adapter subAdapter = new RecentAdapter(textSet, true);

                            recyclerSubView.setAdapter(subAdapter);

                            new Handler().postDelayed(() -> {
                                roomLayout.startAnimation(translateLeftAnim);
                                roomLayout.setVisibility(View.GONE);
                                subLayout.startAnimation(translateLeftAnim_sub);
                                subLayout.setVisibility(View.VISIBLE);
                            }, 200);

                            isSub = true;
                        } else {
                            Toast.makeText(context, "No Recent", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerSubView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerSubView, new RecyclerTouchListener.ClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view, int position) {
                if(!subfName.get(position).equals("date")) {
                    selectedFileName = subfName.get(position);

                    StringBuilder data = new StringBuilder();

                    File file = new File(files, selectedRoomName + '/' + selectedFileName);
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

                    Intent intent = new Intent(context, ChatActivity.class);

                    String recentName;
                    if(selectedFileName.length() > 15)
                        recentName = selectedFileName.substring(16);
                    else
                        recentName = "no_name";

                    String date = "";
                    try {
                        date = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss").format(new SimpleDateFormat("yyyyMMdd_HHmmss").parse(selectedFileName.substring(0, 15)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("recentName", recentName);
                    intent.putExtra("roomName", selectedRoomName);
                    intent.putExtra("date", date);
                    intent.putExtra("items", items);

                    context.startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onBackPressed() {
        if(isSub) {
            roomLayout.startAnimation(translateRightAnim);
            roomLayout.setVisibility(View.VISIBLE);
            subLayout.startAnimation(translateRightAnim_sub);
            subLayout.setVisibility(View.GONE);

            isSub = false;
        } else super.onBackPressed();
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
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
