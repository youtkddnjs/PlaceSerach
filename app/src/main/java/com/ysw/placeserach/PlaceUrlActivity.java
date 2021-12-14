package com.ysw.placeserach;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PlaceUrlActivity extends AppCompatActivity {
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_url);

        String placeUrl =getIntent().getStringExtra("place_url");

        wv=findViewById(R.id.wv);
        wv.setWebViewClient(new WebViewClient());
        wv.setWebChromeClient(new WebChromeClient());

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowContentAccess(true);

        wv.loadUrl(placeUrl);

    }

    @Override
    public void onBackPressed() {
        if(wv.canGoBack()) wv.goBack();
        else super.onBackPressed();
    }
}