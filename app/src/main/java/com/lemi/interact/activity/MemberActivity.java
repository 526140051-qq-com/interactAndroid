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

import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;

    private WebSettings webSettings;

    private ImageView back;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        context = this;
        setContentView(R.layout.activity_member);

        init();
    }

    private void init() {
        webView = findViewById(R.id.member_web);

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
        String url = Api.h5Host + Api.ticket;
        webView.loadUrl(url);

        back = findViewById(R.id.member_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, IndexActivity.class);
                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                    finish();
                    break;
                }
        }
    }

//    private void toWXPay() {
//        iwxapi = WXAPIFactory.createWXAPI(this, null);
//        iwxapi.registerApp(Seeting.APP_ID);
//
//        Runnable payRunnable = new Runnable() {
//            @Override
//            public void run() {
//                PayReq request = new PayReq();
//                request.appId = Seeting.APP_ID;
//                request.partnerId = Seeting.partner_ID;
//                request.prepayId = prepayId;
//                request.packageValue = "Sign=WXPay";
//                request.nonceStr = nonceStr;
//                request.timeStamp = timeStamp;
//                request.sign = sign;
//                iwxapi.sendReq(request);
//            }
//        };
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }

//    @Override
//    public void onResp(BaseResp resp) {
//
//        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            if (resp.errCode == 0) {
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
//            }
//            finish();
//        }
//    }

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
        public void callWxPay(String json) {

            IWXAPI api = WXAPIFactory.createWXAPI(context, "wxe3d1f34f56595a6e");

            try {
                JSONObject jsonObject = new JSONObject(json);

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
    }
}
