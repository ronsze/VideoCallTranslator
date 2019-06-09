package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

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

    ActionBar actionBar;
    private Context context;

    private LinearLayout roomLayout;
    private LinearLayout subLayout;
    private RecyclerView recyclerView;
    private RecyclerView recyclerSubView;
    boolean isSub = false;

    private TextView norecent;
    private TextView barCollaps;
    private TextView barTitle;
    private String selectedRoomName;
    private String selectedFileName;

    private File files;
    private ArrayList<String> fName;
    private ArrayList<String> subfName;

    private RecentAdapter subAdapter;

    private ItemTouchHelperCallback mCallback;
    private ItemTouchHelperExtension mItemTouchHelper;

    private final RecentAdapter.Callback mAdapterCallback = new RecentAdapter.Callback() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onFileSelect(View view, int position) {
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
                View mSquareView1 = barTitle;
                View mSquareView2 = view.findViewById(R.id.txt_content);
                Pair participants1 = new Pair<>(mSquareView1, ViewCompat.getTransitionName(mSquareView1));
                Pair participants2 = new Pair<>(mSquareView2, ViewCompat.getTransitionName(mSquareView2));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(getActivity()), participants1, participants2);

                context.startActivity(intent, options.toBundle());
            }, 200);
        }

        @Override
        public void onDeleteClick(View view, int position) {
            selectedFileName = subfName.get(position);

            String fileDir = files + "/" + selectedRoomName + "/" + selectedFileName;
            File f = new File(fileDir);
            if(f.delete()) {
                if(subfName.get(position - 1).equals("date")) {
                    if(subfName.size() > 2) {
                        if(position + 1 < subfName.size()) {
                            if(subfName.get(position + 1).equals("date")) {
                                --position;
                                subfName.remove(position);
                                subAdapter.doRemove(position);
                            }
                        }
                    } else {
                        --position;
                        subfName.remove(position);
                        subAdapter.doRemove(position);
                    }
                }

                subfName.remove(position);
                subAdapter.doRemove(position);

                if(subfName.size() == 0) {
                    fileDir = files + "/" + selectedRoomName;
                    f = new File(fileDir);
                    if(f.delete()) {
                        fName.remove(selectedRoomName);

                        backSub();
                    } else {
                        Toast.makeText(context, "Delete file failed: " + selectedRoomName, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Delete file failed: " + selectedFileName, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        norecent = controlView.findViewById(R.id.txt_norecent);

        actionBar = ((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        recyclerView = controlView.findViewById(R.id.recycler_view);
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

        fName = new ArrayList<>();

        open();

        subfName = new ArrayList<>();
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context.getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View view, int position) {
                if(!isSub) {
                    if(!fName.isEmpty()) {
                        selectedRoomName = fName.get(position);

                        subOpen();
                    }
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
            if(isSub)
                barTitle.setText(selectedRoomName);
            else
                barTitle.setText("Select Room Name");
        }
    }

    void backSub() {
        roomLayout.startAnimation(translateRightAnim);
        roomLayout.setVisibility(View.VISIBLE);
        subLayout.startAnimation(translateRightAnim_sub);
        subLayout.setVisibility(View.GONE);

        isSub = false;

        refresh();
    }

    void refresh() {
        if(isSub) {
            if(subfName.size() == 0)
                backSub();
            else {
                subOpen();
            }
        } else {
            open();
        }
    }

    private void open() {
        fName.clear();

        files = new File(context.getExternalFilesDir(null), "chat");

        if (files.exists()) {
            if (files.listFiles().length > 0) {
                for (File file : files.listFiles()) {
                    fName.add(file.getName());
                }

                RecyclerView.Adapter adapter = new RecentAdapter(fName, false);
                recyclerView.setAdapter(adapter);

                roomLayout.setVisibility(View.VISIBLE);
                norecent.setVisibility(View.GONE);
            } else {
                roomLayout.setVisibility(View.GONE);
                norecent.setVisibility(View.VISIBLE);
            }
        } else {
            roomLayout.setVisibility(View.GONE);
            norecent.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void subOpen() {
        subfName.clear();

        ArrayList<String> data = new ArrayList<>();
        File subFiles = new File(files, selectedRoomName);

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

            subAdapter = new RecentAdapter(data, true, mAdapterCallback);

            recyclerSubView.setAdapter(subAdapter);

            mCallback = new ItemTouchHelperCallback();
            mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
            mItemTouchHelper.attachToRecyclerView(recyclerSubView);

            if(!isSub) {
                new Handler().postDelayed(() -> {
                    roomLayout.startAnimation(translateLeftAnim);
                    roomLayout.setVisibility(View.GONE);
                    subLayout.startAnimation(translateLeftAnim_sub);
                    subLayout.setVisibility(View.VISIBLE);
                }, 200);

                isSub = true;
            }
        } else {
            if(isSub) {
                String fileDir = files + "/" + selectedRoomName;
                File f = new File(fileDir);
                if(f.delete()) {
                    fName.remove(selectedRoomName);

                    backSub();
                } else {
                    Toast.makeText(context, "Delete file failed: " + selectedRoomName, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.START);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            RecentAdapter.MyViewHolder holder = (RecentAdapter.MyViewHolder) viewHolder;
            if (dX < -holder.mActionContainer.getWidth()) {
                dX = -holder.mActionContainer.getWidth();
            }
            holder.mViewContent.setTranslationX(dX);
        }
    }
}
