package com.lemi.interact.wxpay;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lemi.interact.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class PayActivity extends Activity {

    private IWXAPI api;

    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay);

        api = WXAPIFactory.createWXAPI(this, "wxe3d1f34f56595a6e");

        Intent intent = getIntent();
        json = intent.getStringExtra("json");


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
            Toast.makeText(PayActivity.this, "发起支付成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(PayActivity.this, "发起支付失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
