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
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.util.MyUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import cn.rongcloud.rtc.RTCErrorCode;
import cn.rongcloud.rtc.RongRTCConfig;
import cn.rongcloud.rtc.RongRTCEngine;
import cn.rongcloud.rtc.callback.JoinRoomUICallBack;
import cn.rongcloud.rtc.callback.RongRTCResultUICallBack;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.local.RongRTCCapture;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;

    private WebSettings webSettings;

    private ImageView back;

    private String roomId;

    private String createUserId;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_personal);
        context = this;
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        createUserId = intent.getStringExtra("createUserId");
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
        public String joinRoom(String roomId) {
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString("userId", "");
            OkHttpUtils
                    .post()
                    .url(Api.apiHost + Api.payForRoom)
                    .addParams("roomId", roomId)
                    .addParams("userId", userId)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                            }.getType();
                            ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                            if (apiResult.getCode().intValue() == 0) {

                                if (apiResult.getData() != null){
                                    IWXAPI api = WXAPIFactory.createWXAPI(context, "wxe3d1f34f56595a6e");
                                    try {
                                        JSONObject jsonObject = new JSONObject(apiResult.getData().toString());

                                        PayReq req = new PayReq();
                                        req.appId = jsonObject.getString("appid");
                                        req.partnerId = jsonObject.getString("partnerid");
                                        req.prepayId = jsonObject.getString("prepayid");
                                        req.nonceStr = jsonObject.getString("noncestr");
                                        req.timeStamp = jsonObject.getString("timestamp");
                                        req.packageValue = jsonObject.getString("package");
                                        req.sign = jsonObject.getString("sign");
//                        req.extData			= "app data";
                                        api.sendReq(req);
                                        Toast.makeText(context, "发起支付成功", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(context, "发起支付失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else if(apiResult.getCode().intValue() == 2) {
                                if (apiResult.getData()!=null){
                                    java.lang.reflect.Type roomType = new TypeToken<Room>() {
                                    }.getType();
                                    Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);

                                    String roomID = room.getNum();

                                    Intent intent = new Intent();
                                    intent.putExtra("roomId", roomID);
                                    intent.setClass(context, RoomInfoActivity.class);
                                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                    finish();
                                }
                            }else {
                                Toast.makeText(PersonalActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


            System.out.println(roomId);
            return "";
        }

    }
}
