package com.androidsrc.server;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;



public class TeacherWebView extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_web_view);


        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String id=sharedpreferences.getString("id","null");

        WebView mWebview = (WebView) findViewById(R.id.teacherweb1);
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebview .loadUrl("http://automated-attendance.tk/tprofile/e9bdcecc9821945aab949553d30af2fc/"+id);
    }
}
