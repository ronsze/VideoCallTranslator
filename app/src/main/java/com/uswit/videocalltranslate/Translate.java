package com.uswit.videocalltranslate;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Translate extends Thread {
    private final String apikey = "09d9f87fedec235485dc389a14ed4f34";

    private ThreadReceive mThreadReceive;

    private String str;
    private String src;
    private String target;

    public interface ThreadReceive {
        void onTranslateResult(String result);
        void onTranslateError(String result);
    };

    public Translate(ThreadReceive threadReceive, final String str, final String _src) {
        mThreadReceive = threadReceive;
        this.str = str;
        this.src = _src.equals("ko-KR") ? "kr" : "en";
        this.target = _src.equals("ko-KR") ? "en" : "kr";
    }

    @Override
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

            BufferedReader br;

            if(responseCode==200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String inputLine;
                StringBuilder res = new StringBuilder();

                while((inputLine = br.readLine()) != null) {
                    res.append(inputLine);
                }

                br.close();

                mThreadReceive.onTranslateResult(res.toString().substring(22, res.length() - 4));
            }
            else {
                mThreadReceive.onTranslateError("responseCode: " + responseCode);
            }

        } catch (Exception e) {
            mThreadReceive.onTranslateError(e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
