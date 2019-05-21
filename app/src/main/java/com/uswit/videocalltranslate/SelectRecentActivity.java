package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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
            if(isSub)
                barTitle.setText(selectedRoomName);
            else
                barTitle.setText("Select Room Name");
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private LinearLayout roomLayout;
    private LinearLayout subLayout;
    private RecyclerView recyclerSubView;
    private boolean isSub = false;

    private TextView barTitle;
    private String selectedRoomName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recent);

        Context context = this.getApplicationContext();

        barTitle = findViewById(R.id.barTitle);
        barTitle.setText("Select Room Name");

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

        if (files.listFiles().length > 0) {
            for (File file : files.listFiles()) {
                fName.add(file.getName());
            }

            textSet = fName.toArray(new String[fName.size()]);
            RecyclerView.Adapter adapter = new RecentAdapter(textSet, false);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        String[] finalTextSet = textSet;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view, int position) {
                if(!isSub) {
                    if(finalTextSet != null) {
                        ArrayList<String> subfName = new ArrayList<>();
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

                            for(String file : subfName) {
                                String date = file.substring(0, 8);
                                try {
                                    if(!date.equals(prevDate)) {
                                        data.add('T' + new SimpleDateFormat("yyyy.MM.dd.").format(new SimpleDateFormat("yyyyMMdd").parse(date)));
                                        prevDate = date;
                                    }
                                    String name;
                                    if (file.length() > 15) name = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("HHmmss").parse(file.substring(9, 15))) + "_" + file.substring(16);
                                    else name = new SimpleDateFormat("HH:mm:ss").format(new SimpleDateFormat("HHmmss").parse(file.substring(9, 15)));
                                    data.add('D' + name);
                                } catch (ParseException e) {
                                    Log.e("SelectRecentActivity", e.toString());
                                }
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
            @Override
            public void onClick(View view, int position) {

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
}
