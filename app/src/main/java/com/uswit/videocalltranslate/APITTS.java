package com.uswit.videocalltranslate;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class APITTS {
    private static String TAG = "APITTS";
    public static MediaPlayer audioPlay;

    public static void main(String args, int _type){
        String clientId = "c0hi4hwjhs";
        String clientSecret = "9a3SKgN5ne8UHktEUni2654w9VtpzqnUxvHLR3Oz";

        String postParams;

        int type = _type;
        try{
            String text = URLEncoder.encode(args, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            postParams = "speaker=mijin&speed=0&text=" + text;

            if(type == 1) {
                postParams = "speaker=clara&speed=0&text=" + text;
            }

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                Log.e("APITTS", "정상호출");
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];
                // 랜덤한 이름으로 mp3 파일 생성
                File f = new File(Environment.getExternalStorageDirectory() +  File.separator + "voice.mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                is.close();
            } else {  // 에러 발생
                Log.e("APITTS", "에러");
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                Log.e("APITTS", response.toString());
            }
        } catch (Exception e) {
            Log.e("APITTS", e.toString());
        }
    }
}
