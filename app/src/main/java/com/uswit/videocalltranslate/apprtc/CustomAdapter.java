package com.uswit.videocalltranslate.apprtc;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uswit.videocalltranslate.NaverTTSTask;
import com.uswit.videocalltranslate.R;
import com.uswit.videocalltranslate.Translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private String language;
    private String remoteLang;

    private ArrayList<AdapterContent> items = new ArrayList<>();

    private MediaPlayer mPlayer;

    private Context context;

    private int prevpos;

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    CustomAdapter(String _lang, String _remoteLang, Context _context) {
        language = _lang;
        remoteLang = _remoteLang;
        context = _context;
    }

    public void add(String msg, int type) {
        items.add(new AdapterContent(msg, type));
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
            case 0:
                LayoutInflater inflater_L = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater_L.inflate(R.layout.local_msg_box, parent, false);
                msgBox = convertView.findViewById(R.id.localMsgBox);
                trans = convertView.findViewById(R.id.localTransBtn);
                break;
            case 1:
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
                backgroundHandleMsg(0, prevpos);
                prevpos = pos;
            } else {
                prevpos = pos;
                new Thread() {
                    public void run() {
                        try {
                            NaverTTSTask mNaverTTSTask = new NaverTTSTask(items.get(pos).lang.equals("ko") ? 1 : 0);
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
                            backgroundHandleMsg(1, pos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mPlayer.setOnCompletionListener(mp -> backgroundHandleMsg(0, pos));
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

                if (items.get(pos).lang.equals("ko")) {
                    items.get(pos).lang = "en";
                } else{
                    items.get(pos).lang = "ko";
                }
            }
        });
        return convertView;
    }

    private Handler backHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.arg1;
            int pos = msg.arg2;
        }
    };

    private void backgroundHandleMsg(int type, int pos) {
        Message msg = backHandler.obtainMessage();
        msg.arg1 = type;
        msg.arg2 = pos;
        backHandler.sendMessage(msg);
    }

    class AdapterContent {
        String msg;
        int type;

        String originalText;
        String transText;

        String lang;
        String lang_remote;

        TextView msgBox;

        boolean transOn = false;

        AdapterContent(String _msg, int _type) {
            msg = _msg;
            type = _type;
            lang = language;
            lang_remote = remoteLang;

            originalText = msg;

            String src;
            String target;

            if (language.equals("ko")) {
                if(type == 1){
                    lang = lang_remote;
                }
                src = "en";
                target = "kr";
            } else {
                if(type == 1){
                    lang = lang_remote;
                }
                src = "kr";
                target = "en";
            }

            Translate translate;

            if(type == 0) {
                translate = new Translate(handler, src, target);
            }else{
                translate = new Translate(handler, target, src);
            }

            translate.run(msg);
        }

        Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                super.handleMessage(message);

                switch (message.what) {
                    case R.id.translateCode:
                        Toast.makeText(context, "responseCode >> " + message.arg1, Toast.LENGTH_SHORT).show();

                        break;

                    case R.id.translateResult:
                        transText = message.obj.toString();
                        break;

                    case R.id.translateError:
                        Toast.makeText(context, message.obj.toString(), Toast.LENGTH_SHORT).show();

                        break;

                    default:
                }
            }
        };
    }
}
