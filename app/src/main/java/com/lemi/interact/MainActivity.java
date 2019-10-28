package com.lemi.interact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.activity.IndexActivity;
import com.lemi.interact.activity.LoginActivity;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.util.MainHandler;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.imlib.RongIMClient;

public class MainActivity extends Activity {

    public static final int REQ_CODE_FOR_REGISTER = 1;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("userId","");
        if (userId == null || "".equals(userId)){
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
            finish();
        }else {
            connectRong(Integer.parseInt(userId));
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
                                Toast.makeText(MainActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
                    public void onTokenIncorrect() {}

                    /**
                     * 连接融云成功
                     */
                    @Override
                    public void onSuccess(String userid) {
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
}
