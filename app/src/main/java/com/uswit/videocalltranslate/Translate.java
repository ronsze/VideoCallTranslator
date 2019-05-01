package com.uswit.videocalltranslate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

class Translate {
    private final String apikey = "09d9f87fedec235485dc389a14ed4f34";

    private String src;
    private String target;

    private Handler handler;

    Translate(Handler handler, String src, String target) {
        this.handler = handler;

        this.src = src;
        this.target = target;
    }

    void setParams(String src, String target) {
        this.src = src;
        this.target = target;
    }

    void run(final String str) {
        new Thread() {
            public void run() {
                HttpURLConnection conn = null;

                try {
                    String text = URLEncoder.encode(str, "UTF-8");

                    String postParams = "src_lang=" + src + "&target_lang=" + target + "&query=" + text;
                    String apiURL = "https://kapi.kakao.com/v1/translation/translate?";
                    URL url = new URL(apiURL);

                    Log.e("Translate", apiURL + postParams);

                    conn = (HttpURLConnection) url.openConnection();

                    String basicAuth = "KakaoAK " + apikey;

                    conn.setRequestProperty("Authorization", basicAuth);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(postParams);
                    wr.flush();
                    wr.close();

                    int responseCode = conn.getResponseCode();

                    Message msg = handler.obtainMessage();

                    BufferedReader br;

                    if(responseCode==200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String inputLine;
                        StringBuilder res = new StringBuilder();

                        while((inputLine = br.readLine()) != null) {
                            res.append(inputLine);
                        }

                        br.close();

                        msg.what = R.id.translateResult;
                        msg.obj = res.toString().substring(22, res.length() - 4);

                        handler.sendMessage(msg);
                    }
                    else {
                        msg.what = R.id.translateCode;
                        msg.arg1 = responseCode;

                        handler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    Message msg = handler.obtainMessage();

                    msg.what = R.id.translateError;
                    msg.obj = e.toString();

                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }.start();
    }
}
