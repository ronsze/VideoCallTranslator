package com.uswit.videocalltranslate.apprtc;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uswit.videocalltranslate.NaverTTSTask;
import com.uswit.videocalltranslate.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<AdapterContent> items;

    private Context context;

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    public CustomAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public CustomAdapter(Context context, ArrayList<AdapterContent> items) {
        this.context = context;
        this.items = items;
    }

    public String add(AdapterContent adapterContent) {
        items.add(adapterContent);
        return adapterContent.transText;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == R.id.chat_local)
            return new LocalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.local_msg_box, parent, false), context);
        else {
            return new RemoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.remote_msg_box, parent, false), context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LocalViewHolder) {
            ((LocalViewHolder)holder).onBind(items.get(position));
        } else {
            ((RemoteViewHolder)holder).onBind(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class LocalViewHolder extends MyViewHolder {
        TextView msgBox;
        Button trans;

        LocalViewHolder(View view, Context context) {
            super(view, context);

            msgBox = view.findViewById(R.id.localMsgBox);
            trans = view.findViewById(R.id.localTransBtn);

        }

        void onBind(AdapterContent item) {
            item.msgBox = msgBox;
            super.onBind(item, msgBox, trans);
        }
    }

    public static class RemoteViewHolder extends MyViewHolder {
        TextView msgBox;
        Button trans;

        RemoteViewHolder(View view, Context context) {
            super(view, context);

            msgBox = view.findViewById(R.id.remoteMsgBox);
            trans = view.findViewById(R.id.remoteTransBtn);
        }

        void onBind(AdapterContent item) {
            item.msgBox = msgBox;
            super.onBind(item, msgBox, trans);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private MediaPlayer mPlayer;

        MyViewHolder(View view, Context context) {
            super(view);

            mPlayer = new MediaPlayer();

            try {
                mPlayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "voice.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }

            AudioManager audio;
            audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2, 0);
        }

        void onBind(AdapterContent item, TextView msgBox, Button trans) {
            msgBox.setText(item.originalText);


            msgBox.setOnClickListener(v -> {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                } else {
                    new Thread() {
                        public void run() {
                            try {
                                NaverTTSTask mNaverTTSTask = new NaverTTSTask(item.lang.equals("ko-KR") ? 0 : 1);
                                if (item.transOn)
                                    mNaverTTSTask.setText(item.transText);
                                else
                                    mNaverTTSTask.setText(item.originalText);
                                mNaverTTSTask.execute();

                                Thread.sleep(200);
                                if (mPlayer != null) {
                                    mPlayer.release();
                                    mPlayer = null;
                                }
                                mPlayer = new MediaPlayer();
                                mPlayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "voice.mp3");
                                mPlayer.prepare();
                                mPlayer.setVolume(1.0f, 1.0f);
                                mPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });

            trans.setOnClickListener(v -> {
                if (!(mPlayer.isPlaying())) {
                    if (item.transOn) {
                        item.msgBox.setText(item.originalText);
                        item.transOn = false;
                    } else{
                        item.msgBox.setText(item.transText);
                        item.transOn = true;
                    }

                    if (item.lang.equals("ko-KR")) {
                        item.lang = "en-US";
                    } else{
                        item.lang = "ko-KR";
                    }
                }
            });
        }
    }
}

