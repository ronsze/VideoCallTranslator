package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_DATA = 0;
    private final static int TYPE_TIME = 1;

    private String[] textSet;
    private boolean isSub;

    RecentAdapter(String[] textSet, boolean isSub) {
        this.textSet = textSet;
        this.isSub = isSub;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TIME) {
            return new TimeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.time_view, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_file_view, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder)
            ((MyViewHolder)holder).onBind(textSet[position].substring(1), isSub);
        else
            ((TimeViewHolder)holder).onBind(textSet[position].substring(1));
    }

    @Override
    public int getItemCount() {
        return textSet.length;
    }

    @Override
    public int getItemViewType(int position) {
        if(isSub) {
            return textSet[position].charAt(0) == 'D' ? TYPE_DATA : TYPE_TIME;
        } else {
            return TYPE_DATA;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private TextView time;

        MyViewHolder(View view) {
            super(view);

            this.title = view.findViewById(R.id.txt_title);
            this.content = view.findViewById(R.id.txt_content);
            this.time = view.findViewById(R.id.txt_time);
        }

        @SuppressLint({"SetTextI18n"})
        void onBind(String text, boolean isSub) {
            if(isSub) {
                title.setText("Name:");
                if(text.contains("_"))
                    content.setText(text.split("_")[1]);
                else
                    content.setText("no_name");
                time.setText(text.split("_")[0]);
            } else {
                content.setText(text);
            }
        }
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        TextView timeItemView;

        TimeViewHolder(View view) {
            super(view);

            timeItemView = view.findViewById(R.id.timeView);
        }

        void onBind(String text) {
            timeItemView.setText(text);
        }
    }
}
