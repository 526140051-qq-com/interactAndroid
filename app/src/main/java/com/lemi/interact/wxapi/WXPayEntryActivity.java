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

//    private String roomID;
//
//    private boolean isPay;

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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            String payType = sharedPreferences.getString("pay_type", "");
            if (resp.errCode == 0) {
                if (payType.equals("charge")) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), RoomActivity.class);
                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                    finish();
                } else if (payType.equals("room")) {
//                    final String out_trade_no = sharedPreferences.getString("out_trade_no", "");
//                    final String userId = sharedPreferences.getString("userId", "");
//                    boolean flag = false;
//                    for (int i = 0; i < 10; i++) {
//                        flag = isPayForRoom(out_trade_no);
//                        if (flag) {
//                            connectRong(roomID, userId);
//                            break;
//                        }
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (!flag){
//                        Toast.makeText(context, "支付超时", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.setClass(getApplicationContext(), RoomActivity.class);
//                        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
//                        finish();
//                    }

                    final String out_trade_no = sharedPreferences.getString("out_trade_no", "");
                    final String userId = sharedPreferences.getString("userId", "");
                    final String room_num = sharedPreferences.getString("room_num", "");
                    connectRong(room_num, userId);
                    editor.remove("out_trade_no");
                    editor.remove("room_num");
                    editor.commit();
                } else if (payType.equals("charge_again")) {
                    finish();
                }else if (payType.equals("buy_gift")) {
                    finish();
                }
            } else {
                if (payType.equals("room")) {
                    final String out_trade_no = sharedPreferences.getString("out_trade_no", "");
                    final String room_num = sharedPreferences.getString("room_num", "");
                    OkHttpUtils
                            .post()
                            .url(Api.apiHost + Api.removeRoomJoinByNum)
                            .addParams("roomNum", room_num)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(okhttp3.Call call, Exception e, int id) {
                                }
                                @Override
                                public void onResponse(String response, int id) {
                                }
                            });
                    editor.remove("out_trade_no");
                    editor.remove("room_num");
                    editor.commit();
                }
                finish();
            }
        }
        editor.remove("pay_type");
        editor.commit();
    }

//    private boolean isPayForRoom(String outTradeNo) {
//        OkHttpUtils
//                .post()
//                .url(Api.apiHost + Api.isPayForRoom)
//                .addParams("outTradeNo", outTradeNo)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(okhttp3.Call call, Exception e, int id) {
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        java.lang.reflect.Type type = new TypeToken<ApiResult>() {
//                        }.getType();
//                        ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
//                        if (apiResult.getCode().intValue() == 0) {
//                            if (apiResult.getData() != null) {
//                                java.lang.reflect.Type roomType = new TypeToken<Room>() {
//                                }.getType();
//                                Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
//                                roomID = room.getNum();
//                                isPay = true;
//                            }
//                        } else {
//                            isPay = false;
//                        }
//                    }
//                });
//        return isPay;
//    }

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