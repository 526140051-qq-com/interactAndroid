package com.lemi.interact.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lemi.interact.R;
import com.lemi.interact.api.Api;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener{

    private WebView webView;

    private WebSettings webSettings;

    private ImageView back;

    private String roomId;

    private String createUserId;

    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_personal);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        createUserId = intent.getStringExtra("createUserId");
        categoryId = intent.getStringExtra("categoryId");
        init();
    }

    private void init() {
        webView = findViewById(R.id.personal_web);

        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(getApplicationContext().getCacheDir().getAbsolutePath());
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";userId=" + userId);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(2);
        }
        webView.addJavascriptInterface(new JsInteration(), "android");
        webView.setWebViewClient(new MyWebViewClient());
        String url = Api.h5Host + Api.personal + "/" + roomId + "/" + createUserId;
        webView.loadUrl(url);

        back = findViewById(R.id.personal_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("categoryId",categoryId);
                    intent.setClass(this, RoomActivity.class);
                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                    finish();
                    break;
                }
        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
        }
    }

    public class JsInteration {
        @JavascriptInterface
        public String toWXPay(Integer chargeId) {

            return "";
        }
    }
}
