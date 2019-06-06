package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class SelectRecentFragment extends Fragment {

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
    private Context context;

    private LinearLayout roomLayout;
    private LinearLayout subLayout;
    private RecyclerView recyclerSubView;
    boolean isSub = false;

    private TextView barCollaps;
    private TextView barTitle;
    private String selectedRoomName;
    private String selectedFileName;
    private ArrayList<String> subfName;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_select_recent, container, false);

        context = this.getContext();

        RelativeLayout layout = controlView.findViewById(R.id.recent_layout);
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        float px = 250 * (metrics.densityDpi / 160f);
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(width,(int)(height-px));
        layout.setLayoutParams(parms);

        barCollaps = controlView.findViewById(R.id.barCollaps);
        barTitle = controlView.findViewById(R.id.barTitle);

        Toolbar toolbar = controlView.findViewById(R.id.recent_toolbar);
        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);

        actionBar = ((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        RecyclerView recyclerView = controlView.findViewById(R.id.recycler_view);
        recyclerSubView = controlView.findViewById(R.id.recycler_view_sub);

        translateLeftAnim = AnimationUtils.loadAnimation(context, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(context, R.anim.translate_right);
        translateLeftAnim_sub = AnimationUtils.loadAnimation(context, R.anim.sub_translate_left);
        translateRightAnim_sub = AnimationUtils.loadAnimation(context, R.anim.sub_translate_right);

        translateLeftAnim_sub.setAnimationListener(animationListener);
        translateRightAnim_sub.setAnimationListener(animationListener);

        roomLayout = controlView.findViewById(R.id.roomLayout);
        subLayout = controlView.findViewById(R.id.subLayout);

        recyclerView.setHasFixedSize(true);
        recyclerSubView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.LayoutManager subLayoutManager = new LinearLayoutManager(context);
        recyclerSubView.setLayoutManager(subLayoutManager);

        collapsTitle(((MainActivity) Objects.requireNonNull(getActivity())).isBottomCollapsed);

        ArrayList<String> fName = new ArrayList<>();
        File files = new File(context.getExternalFilesDir(null), "chat");

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
                TextView norecent = controlView.findViewById(R.id.txt_norecent);
                norecent.setVisibility(View.VISIBLE);
            }
        } else {
            roomLayout.setVisibility(View.GONE);
            TextView norecent = controlView.findViewById(R.id.txt_norecent);
            norecent.setVisibility(View.VISIBLE);
        }

        subfName = new ArrayList<>();
        String[] finalTextSet = textSet;
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context.getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
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
                                    Log.e("SelectRecentFragment", e.toString());
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

        recyclerSubView.addOnItemTouchListener(new RecyclerTouchListener(context.getApplicationContext(), recyclerSubView, new RecyclerTouchListener.ClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view, int position) {
                if(!subfName.get(position).equals("date")) {
                    selectedFileName = subfName.get(position);

                    String fileDir = files + "/" + selectedRoomName + "/" + selectedFileName;

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
                    intent.putExtra("fileDir", fileDir);

                    new Handler().postDelayed(() -> {
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), view, "transition");
                        int revealX = (int) (view.getX() + view.getWidth() / 2);
                        int revealY = (int) (view.getY() + (300 * (metrics.densityDpi / 160f)));

                        intent.putExtra(ChatActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
                        intent.putExtra(ChatActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

                        context.startActivity(intent, options.toBundle());
                    }, 200);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return controlView;
    }

    void collapsTitle(boolean toggle) {
        if(toggle) {
            barCollaps.setText(R.string.chat_record);
            barTitle.setText("");
        } else {
            barCollaps.setText("");
            barTitle.setText("Select Room Name");
        }
    }

    void backSub() {
        roomLayout.startAnimation(translateRightAnim);
        roomLayout.setVisibility(View.VISIBLE);
        subLayout.startAnimation(translateRightAnim_sub);
        subLayout.setVisibility(View.GONE);

        isSub = false;
    }
}
