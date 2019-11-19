package com.lemi.interact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.util.CountDownTimerUtils;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.util.PhoneUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class ForgetActivity extends Activity implements View.OnClickListener {

    private EditText phone;

    private EditText pwd;

    private EditText code;

    private Button codeBtn;

    private Button forgetBtn;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget);
        context = this;
        init();
    }

    private void init() {
        phone = findViewById(R.id.forget_phone);
        phone.setHintTextColor(Color.parseColor("#a5ffffff"));

        pwd = findViewById(R.id.forget_pwd);
        pwd.setHintTextColor(Color.parseColor("#a5ffffff"));

        code = findViewById(R.id.forget_code);
        code.setHintTextColor(Color.parseColor("#a5ffffff"));

        codeBtn = findViewById(R.id.forget_code_btn);
        codeBtn.setOnClickListener(this);

        forgetBtn = findViewById(R.id.forget_button);
        forgetBtn.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.forget_code_btn:
                String phoneNum = phone.getText().toString();
                if (phoneNum == null || "".equals(phoneNum.trim())) {
                    Toast.makeText(ForgetActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PhoneUtil.isMobileNO(phoneNum)) {
                    Toast.makeText(ForgetActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                codeBtn.setEnabled(false);
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.sendMsg)
                        .addParams("phone", phoneNum)
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
                                    String num = (String) apiResult.getData();
                                    Toast.makeText(ForgetActivity.this, "验证码发送成功：" + num, Toast.LENGTH_SHORT).show();
                                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(codeBtn, 60000, 1000);
                                    mCountDownTimerUtils.start();
                                } else {
                                    codeBtn.setEnabled(true);
                                    Toast.makeText(ForgetActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.forget_button:
                String phoneNum1 = phone.getText().toString();
                String codeNum = code.getText().toString();
                String password = pwd.getText().toString();
                if (phoneNum1 == null || "".equals(phoneNum1.trim())) {
                    Toast.makeText(ForgetActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PhoneUtil.isMobileNO(phoneNum1)) {
                    Toast.makeText(ForgetActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (codeNum == null || "".equals(codeNum.trim())) {
                    Toast.makeText(ForgetActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password == null || "".equals(password.trim())) {
                    Toast.makeText(ForgetActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                forgetBtn.setEnabled(false);
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.forgetPassword)
                        .addParams("phone", phoneNum1)
                        .addParams("code", codeNum)
                        .addParams("password", password)
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
                                    Toast.makeText(ForgetActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.setClass(context, LoginActivity.class);
                                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                    finish();
                                } else {
                                    Toast.makeText(ForgetActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                forgetBtn.setEnabled(true);
                            }
                        });
                break;
        }


    }
}
