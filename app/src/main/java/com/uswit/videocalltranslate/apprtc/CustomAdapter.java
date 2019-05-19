package com.uswit.videocalltranslate.apprtc;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.uswit.videocalltranslate.NaverTTSTask;
import com.uswit.videocalltranslate.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<AdapterContent> items = new ArrayList<>();

    private MediaPlayer mPlayer;

    private Context context;

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    CustomAdapter(Context _context) {
        context = _context;
    }

    public void add(AdapterContent adapterContent) {
        items.add(adapterContent.get());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;

        TextView msgBox = null;
        Button trans = null;

        switch (items.get(pos).type) {
            case R.id.chat_local:
                LayoutInflater inflater_L = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater_L.inflate(R.layout.local_msg_box, parent, false);
                msgBox = convertView.findViewById(R.id.localMsgBox);
                trans = convertView.findViewById(R.id.localTransBtn);
                break;
            case R.id.chat_remote:
                LayoutInflater inflater_R = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater_R.inflate(R.layout.remote_msg_box, parent, false);
                msgBox = convertView.findViewById(R.id.remoteMsgBox);
                trans = convertView.findViewById(R.id.remoteTransBtn);
                break;
        }

        items.get(pos).msgBox = msgBox;

        assert msgBox != null;
        msgBox.setText(items.get(pos).msg);

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

        msgBox.setOnClickListener(v -> {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            } else {
                new Thread() {
                    public void run() {
                        try {
                            NaverTTSTask mNaverTTSTask = new NaverTTSTask(items.get(pos).lang.equals("ko-KR") ? 0 : 1);
                            mNaverTTSTask.setText(items.get(pos).msg);
                            mNaverTTSTask.execute();

                            Thread.sleep(200);
                            if(mPlayer != null){
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



        //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        assert trans != null;
        trans.setOnClickListener(v -> {
            if (!(mPlayer.isPlaying())) {
                if (items.get(pos).transOn) {
                    items.get(pos).msg = items.get(pos).originalText;
                    items.get(pos).msgBox.setText(items.get(pos).originalText);
                    items.get(pos).transOn = false;
                } else{
                    items.get(pos).msg = items.get(pos).transText;
                    items.get(pos).msgBox.setText(items.get(pos).transText);
                    items.get(pos).transOn = true;
                }

                if (items.get(pos).lang.equals("ko-KR")) {
                    items.get(pos).lang = "en-US";
                } else{
                    items.get(pos).lang = "ko-KR";
                }
            }
        });
        return convertView;
    }

}

