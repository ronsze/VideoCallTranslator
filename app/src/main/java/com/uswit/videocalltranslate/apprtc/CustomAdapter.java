package com.uswit.videocalltranslate.apprtc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.uswit.videocalltranslate.APITTS;
import com.uswit.videocalltranslate.MainActivity;
import com.uswit.videocalltranslate.NaverTTSTask;
import com.uswit.videocalltranslate.R;
import com.uswit.videocalltranslate.Translate;
import com.uswit.videocalltranslate.apprtc.CallActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    TextView msgBox;

    String language;
    String src = "kr";
    String target = "en";

    boolean toggleTrans = true;

    ArrayList<AdapterContent> items = new ArrayList<>();

    MediaPlayer mPlayer;

    Context context;

    int prevpos;

    public static String sendText;

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    CustomAdapter(String _lang, Context _context) {
        language = _lang;
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

        msgBox = null;
        Button play_stop = null;
        Button trans = null;

        switch (items.get(pos).type) {
            case 0:
                LayoutInflater inflater_L = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater_L.inflate(R.layout.local_msg_box, parent, false);
                msgBox = (TextView) convertView.findViewById(R.id.localMsgBox);
                play_stop = (Button) convertView.findViewById(R.id.localStartBtn);
                trans = (Button) convertView.findViewById(R.id.localTransBtn);
                break;
            case 1:
                LayoutInflater inflater_R = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater_R.inflate(R.layout.remote_msg_box, parent, false);
                msgBox = (TextView) convertView.findViewById(R.id.remoteMsgBox);
                play_stop = (Button) convertView.findViewById(R.id.remoteStartBtn);
                trans = (Button) convertView.findViewById(R.id.remoteTransBtn);
                break;
        }

        items.get(pos).play_stop = play_stop;
        items.get(pos).msgBox = msgBox;

        msgBox.setText(items.get(pos).msg);

        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(Environment.getExternalStorageDirectory() + File.separator + "voice.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioManager audio = null;
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2, 0);

        play_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                    backgroundHandleMsg(0, prevpos);
                    prevpos = pos;
                } else {
                    prevpos = pos;
                    new Thread() {
                        public void run() {
                            try {
                                NaverTTSTask mNaverTTSTask = new NaverTTSTask(items.get(pos).lang.equals("ko") ? 0 : 1);
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

                            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    backgroundHandleMsg(0, pos);
                                }
                            });
                        }
                    }.start();
                }
            }
        });



        //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        return convertView;
    }

    Handler backHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = msg.arg1;
            int pos = msg.arg2;

            switch (type) {
                case 0:
                    items.get(pos).play_stop.setBackgroundResource(R.drawable.play);
                    break;
                case 1:
                    items.get(pos).play_stop.setBackgroundResource(R.drawable.stop);
                    break;
            }
        }
    };

    void backgroundHandleMsg(int type, int pos) {
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

        Button play_stop;
        TextView msgBox;

        boolean transOn = false;

        AdapterContent(String _msg, int _type) {
            msg = _msg;
            type = _type;
            lang = language;

            originalText = msg;

            if (language.equals("ko")) {
                if(type == 1){
                    lang = "en";
                }
                src = "en";
                target = "kr";
            } else {
                if(type == 1){
                    lang = "ko";
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
