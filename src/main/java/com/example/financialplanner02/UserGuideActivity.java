package com.example.financialplanner02;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class UserGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userguide);

        WebView userGuideWebView = findViewById(R.id.webView);
        userGuideWebView.getSettings().setJavaScriptEnabled(true);
        userGuideWebView.loadUrl("file:///android_asset/user_guide.html");
    }
}
