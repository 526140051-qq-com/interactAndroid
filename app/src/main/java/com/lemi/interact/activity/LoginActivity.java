package com.lemi.interact.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.lemi.interact.util.MainHandler;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.util.PhoneUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import io.rong.imlib.RongIMClient;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class LoginActivity extends Activity implements View.OnClickListener , EasyPermissions.PermissionCallbacks{


    private EditText phone;

    private EditText pwd;

    private Button login;
    String[] params = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private TextView register;

    private TextView forgetPwd;

    private Context context;

    private static final int RC_CAMERA_AND_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        context = this;
        methodRequiresTwoPermission();
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
                login.setEnabled(false);
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
                                    Number num = (Number) apiResult.getData();
                                    Integer userId = num.intValue();
                                    SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userId", userId + "");
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(context, IndexActivity.class);
                                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                } else {
                                    Toast.makeText(LoginActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                login.setEnabled(true);
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

    private void connectRong(final Integer userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("im_token", "");
        if (token != null && !"".equals(token)) {
            connect(token, userId);
        } else {
            OkHttpUtils
                    .post()
                    .url(Api.apiHost + Api.getToken)
                    .addParams("userId", userId + "")
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
                                String token = apiResult.getData().toString();
                                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("im_token", token);
                                editor.commit();
                                connect(token, userId);
                            } else {
                                Toast.makeText(LoginActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            login.setEnabled(true);
                        }
                    });
        }

    }

    private void connect(final String token, final Integer userId) {
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {

                RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                    }

                    /**
                     * 连接融云成功
                     */
                    @Override
                    public void onSuccess(String userid) {
                        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", userId + "");
                        editor.commit();
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(context, IndexActivity.class);
                        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                        finish();
                    }

                    /**
                     * 连接融云失败
                     */
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                        System.out.println(errorCode.getMessage());

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        if (EasyPermissions.hasPermissions(this, params)) {
        } else {
            EasyPermissions.requestPermissions(this, "应用需要权限",
                    RC_CAMERA_AND_LOCATION, params);

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
