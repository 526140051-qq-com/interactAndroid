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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.MainActivity;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.util.PhoneUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class LoginActivity extends Activity implements View.OnClickListener {


    private EditText phone;

    private EditText pwd;

    private Button login;

    private TextView register;

    private TextView forgetPwd;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        context = this;
        init();
    }

    private void init() {
        phone = findViewById(R.id.phone);
        phone.setHintTextColor(Color.parseColor("#a5ffffff"));

        pwd = findViewById(R.id.pwd);
        pwd.setHintTextColor(Color.parseColor("#a5ffffff"));

        login = findViewById(R.id.login_button);
        login.setOnClickListener(this);

        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        forgetPwd = findViewById(R.id.forget_pwd);
        forgetPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                String phoneNum = phone.getText().toString();

                if (phoneNum == null || "".equals(phoneNum.trim())) {
                    Toast.makeText(LoginActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!PhoneUtil.isMobileNO(phoneNum)) {
                    Toast.makeText(LoginActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = pwd.getText().toString();
                if (password == null || "".equals(password.trim())) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.login)
                        .addParams("phone", phoneNum)
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
                                    Number num= (Number)apiResult.getData();
                                    Integer userId = num.intValue();
                                    SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userId", userId+"");
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(context, IndexActivity.class);
                                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                } else {
                                    Toast.makeText(LoginActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.register:
                Intent intent = new Intent();
                intent.setClass(this, RegisterActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                break;

            case R.id.forget_pwd:
                Intent intent1 = new Intent();
                intent1.setClass(this, ForgetActivity.class);
                startActivityForResult(intent1, REQ_CODE_FOR_REGISTER);
                break;

        }
    }
}
