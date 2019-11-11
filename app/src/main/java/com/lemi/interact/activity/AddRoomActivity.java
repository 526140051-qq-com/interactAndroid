package com.lemi.interact.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.util.FastBlur;
import com.lemi.interact.util.MainHandler;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.imlib.RongIMClient;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class AddRoomActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linearLayout;

    ImageButton addRoomBack;

    private String mcategoryId;

    Context context;

    Button addRoomBtn;

    EditText price;

    private LocationManager lm;

    private Double longitude;

    private Double latitude;

    private String roomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String categoryId = sharedPreferences.getString("categoryId", "");
        mcategoryId = categoryId;

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        linearLayout = (LinearLayout) findViewById(R.id.add_room_ll);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.bg_login);
        final Bitmap bitmap1 = FastBlur.fastblur(this, bitmap, 15);
        linearLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        linearLayout.setBackground(new BitmapDrawable(getResources(), bitmap1));
                        return true;
                    }
                });

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isGpsAble(lm)) {
            Toast.makeText(AddRoomActivity.this, "请打开手机定位功能", Toast.LENGTH_SHORT).show();
            openGPS2();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location == null){
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        init();
    }

    private void init() {
        addRoomBack = (ImageButton) findViewById(R.id.add_room_back);
        addRoomBack.setOnClickListener(this);

        addRoomBtn = (Button) findViewById(R.id.add_room1_btn);
        addRoomBtn.setOnClickListener(this);

        price = (EditText) findViewById(R.id.price);
        if (mcategoryId.equals("4")) {
            price.setEnabled(false);
        } else {
            price.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_room_back:
                Intent intent = new Intent();
                intent.setClass(context, RoomActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                finish();
                break;
            case R.id.add_room1_btn:
                String pri = price.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                final String userId = sharedPreferences.getString("userId", "");
                Integer isFree = 1;
                if (mcategoryId.equals("4")) {
                    if (pri == null || "".equals(pri)) {
                        isFree = 1;
                    } else if (Integer.parseInt(pri) == 0) {
                        isFree = 1;
                    } else {
                        isFree = 2;
                    }
                }

                if (pri == null || "".equals(pri)) {
                    pri = "0";
                }
                addRoomBtn.setEnabled(false);
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.addRoom)
                        .addParams("userId", userId)
                        .addParams("categoryId", mcategoryId)
                        .addParams("isFree", isFree + "")
                        .addParams("price", pri)
                        .addParams("longitude", longitude + "")
                        .addParams("latitude", latitude + "")
                        .addParams("roomId",roomId)
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
                                        connectRong(roomID, userId);
                                    }
                                } else {
                                    Toast.makeText(AddRoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                addRoomBtn.setEnabled(true);
                            }
                        });


                break;
        }
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
                        intent.setClass(context, RoomInfoActivity.class);
                        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                        finish();
                        Toast.makeText(AddRoomActivity.this, "创建房间成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        System.out.println(errorCode.getMessage());
                    }
                });
            }
        });
    }

    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true : false;
    }

    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }
}
