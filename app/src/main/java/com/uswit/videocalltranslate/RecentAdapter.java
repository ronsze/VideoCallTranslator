package com.uswit.videocalltranslate;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopeer.itemtouchhelperextension.Extension;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_DATA = 0;
    private final static int TYPE_TIME = 1;

    private ArrayList<String> textSet;
    private boolean isSub;

    private Callback mCallback;

    RecentAdapter(ArrayList<String> textSet, boolean isSub) {
        this.textSet = textSet;
        this.isSub = isSub;
    }

    RecentAdapter(ArrayList<String> textSet, boolean isSub, Callback callback) {
        this.textSet = textSet;
        this.isSub = isSub;
        this.mCallback = callback;
    }

    void doRemove(int position) {
        textSet.remove(position);
        notifyItemRemoved(position);
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
        if(isSub) {
            if(holder instanceof MyViewHolder) {
                ((MyViewHolder)holder).onBind(textSet.get(position).substring(1), true);
                ((MyViewHolder) holder).mViewContent.setOnClickListener(view -> mCallback.onFileSelect(view, holder.getAdapterPosition()));
                ((MyViewHolder) holder).mActionContainer.setOnClickListener(view -> mCallback.onDeleteClick(view, holder.getAdapterPosition()));
            }
            else
                ((TimeViewHolder)holder).onBind(textSet.get(position).substring(1));
        } else {
            ((MyViewHolder)holder).onBind(textSet.get(position), false);
        }
    }

    @Override
    public int getItemCount() {
        return textSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isSub) {
            return textSet.get(position).charAt(0) == 'D' ? TYPE_DATA : TYPE_TIME;
        } else {
            return TYPE_DATA;
        }
    }

    public abstract static class Callback {
        public void onFileSelect(View view, int position) {}
        public void onDeleteClick(View view, int position) {}
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements Extension {
        private TextView title;
        private TextView content;
        private TextView time;
        RelativeLayout mViewContent;
        RelativeLayout mActionContainer;

        MyViewHolder(View view) {
            super(view);

            this.title = view.findViewById(R.id.txt_title);
            this.content = view.findViewById(R.id.txt_content);
            this.time = view.findViewById(R.id.txt_time);
            this.mViewContent = view.findViewById(R.id.contentLayout);
            this.mActionContainer = view.findViewById(R.id.actionLayout);
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

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
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
