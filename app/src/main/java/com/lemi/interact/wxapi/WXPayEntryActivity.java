package com.lemi.interact.wxapi;


import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.activity.RoomActivity;
import com.lemi.interact.activity.RoomInfoActivity;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.util.MainHandler;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.wxpay.Constants;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;


import io.rong.imlib.RongIMClient;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
        context = this;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                String payType = sharedPreferences.getString("pay_type", "");
                if (payType.equals("charge")) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), RoomActivity.class);
                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                    finish();
                } else if (payType.equals("room")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final String roomId = sharedPreferences.getString("pay_room_id", "");
                    final String userId = sharedPreferences.getString("userId", "");
                    OkHttpUtils
                            .post()
                            .url(Api.apiHost + Api.isPayForRoom)
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
                                        if (apiResult.getData() != null) {
                                            java.lang.reflect.Type roomType = new TypeToken<Room>() {
                                            }.getType();
                                            Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
                                            String roomID = room.getNum();
                                            if (roomID != null && !"".equals(roomID)) {
                                                connectRong(roomID, userId);
                                            }
                                        }
                                    }
                                }
                            });



                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("pay_room_id");
                    editor.commit();
                }
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("pay_type");
        editor.commit();
    }

    private void connectRong(final String roomId, final String userId) {
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
                            connect(roomId, token);
                        } else {
                            Toast.makeText(context, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void connect(final String roomId, final String token) {
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {

                RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                    }

                    @Override
                    public void onSuccess(String userid) {
                        Intent intent = new Intent();
                        intent.putExtra("roomId", roomId);
                        intent.setClass(getApplicationContext(), RoomInfoActivity.class);
                        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                        finish();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        System.out.println(errorCode.getMessage());
                    }
                });
            }
        });
    }
}