package com.lemi.interact.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.adapter.RecyclerViewGridAdapter;
import com.lemi.interact.adapter.SpacesItemDecoration;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.RoomResponse;
import com.lemi.interact.dialog.AddressPickerDialog;
import com.lemi.interact.util.MyUtils;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;

    private List<RoomResponse> roomList;

    private ImageButton addRoomBtn;

    private ImageView roomListBack;

    private EditText search;

    private Context context;

    private LocationManager lm;

    private Double longitude;

    private Double latitude;

    private String categoryId;

    private LinearLayout roomCity;

    private TextView roomCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        context = this;
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!isGpsAble(lm)) {
            Toast.makeText(RoomActivity.this, "请打开手机定位功能", Toast.LENGTH_SHORT).show();
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
        if (location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        categoryId=sharedPreferences.getString("categoryId","");
        if (categoryId != null && !"".equals(categoryId)) {
            initData(categoryId);
        }

    }
    private void initData(final String categoryId) {
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        roomCityName = (TextView) findViewById(R.id.room_city_name);
        if (longitude != null && latitude != null){
            OkHttpUtils
                    .post()
                    .url(Api.apiHost + Api.getCityNameByL)
                    .addParams("longitude", longitude + "")
                    .addParams("latitude", latitude + "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(okhttp3.Call call, Exception e, int id) {
                            Toast.makeText(RoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                            }.getType();
                            ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                            if (apiResult.getCode().intValue() == 0) {
                                String city = apiResult.getData().toString();
                                roomCityName.setText(city.trim());
                            } else {
                                roomCityName.setText("全部");
                            }
                            initRoom();
                        }
                    });
        }else {
            roomCityName.setText("全部");
            initRoom();
        }


        addRoomBtn = (ImageButton) findViewById(R.id.add_room_btn);
        addRoomBtn.setOnClickListener(this);

        roomListBack = (ImageView) findViewById(R.id.room_list_back);
        roomListBack.setOnClickListener(this);

        search = (EditText) findViewById(R.id.search);
        search.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(),0);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //输入时的调用
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mHandler.removeCallbacks(mRunnable);

                mHandler.postDelayed(mRunnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        roomCity = (LinearLayout) findViewById(R.id.room_city);
        roomCity.setOnClickListener(this);


    }

    private void initRoom(){
        String city = roomCityName.getText().toString();
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findRoom)
                .addParams("categoryId", categoryId)
                .addParams("longitude", longitude + "")
                .addParams("latitude", latitude + "")
                .addParams("city",city)
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
                            String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                            RoomResponse[] array = MyUtils.getGson().fromJson(jsonString, RoomResponse[].class);
                            roomList = Arrays.asList(array);
                            loadgrideDate(false, true,categoryId);
                        } else {
                            Toast.makeText(RoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
            String num = search.getText().toString();
            String city = roomCityName.getText().toString();
            if (num!=null&&!"".equals(num.trim())){
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.findRoom)
                        .addParams("categoryId", categoryId)
                        .addParams("longitude", longitude + "")
                        .addParams("latitude", latitude + "")
                        .addParams("num",num)
                        .addParams("city",city)
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
                                    String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                                    RoomResponse[] array = MyUtils.getGson().fromJson(jsonString, RoomResponse[].class);
                                    roomList = Arrays.asList(array);
                                    loadgrideDate(false, true,categoryId);
                                } else {
                                    Toast.makeText(RoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.findRoom)
                        .addParams("categoryId", categoryId)
                        .addParams("longitude", longitude + "")
                        .addParams("latitude", latitude + "")
                        .addParams("city",city)
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
                                    String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                                    RoomResponse[] array = MyUtils.getGson().fromJson(jsonString, RoomResponse[].class);
                                    roomList = Arrays.asList(array);
                                    loadgrideDate(false, true,categoryId);
                                } else {
                                    Toast.makeText(RoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    };

    private void loadgrideDate(Boolean isversion, Boolean orientation,String categoryId) {
        recyclerView.addItemDecoration(new SpacesItemDecoration(5, 5));

        RecyclerViewGridAdapter recyclerViewGridAdapter = new RecyclerViewGridAdapter(this,this,categoryId, roomList);
        recyclerView.setAdapter(recyclerViewGridAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setReverseLayout(isversion);
        gridLayoutManager.setOrientation(orientation ? GridLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);

    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_room_btn:
                addRoomBtn.setEnabled(false);
                Intent intent = new Intent();
                intent.setClass(context, AddRoomActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                addRoomBtn.setEnabled(true);
                break;
            case R.id.room_list_back:
                Intent intent1 = new Intent();
                intent1.setClass(this, IndexActivity.class);
                startActivityForResult(intent1, REQ_CODE_FOR_REGISTER);
                finish();
                break;
            case R.id.room_city:
                selectAddress();
                break;
        }
    }

    private void selectAddress() {
        CityPicker cityPicker = new CityPicker.Builder(context)
                .textSize(14)
                .title("地址选择")
                .titleBackgroundColor("#FFFFFF")
                .confirTextColor("#696969")
                .cancelTextColor("#696969")
                .province("江苏省")
                .city("苏州市")
                .textColor(Color.parseColor("#000000"))
                .provinceCyclic(true)
                .cityCyclic(false)
                .visibleItemsCount(7)
                .itemPadding(15)
                .onlyShowProvinceAndCity(true)
                .build();
        cityPicker.show();
        //监听方法，获取选择结果
        cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
            @Override
            public void onSelected(String... citySelected) {
                //城市
                String city = citySelected[1];

                //为TextView赋值
                roomCityName.setText(city.trim());
                String num = search.getText().toString();

                if (num!=null&&!"".equals(num.trim())){
                    OkHttpUtils
                            .post()
                            .url(Api.apiHost + Api.findRoom)
                            .addParams("categoryId", categoryId)
                            .addParams("longitude", longitude + "")
                            .addParams("latitude", latitude + "")
                            .addParams("num",num)
                            .addParams("city",city.trim())
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
                                        String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                                        RoomResponse[] array = MyUtils.getGson().fromJson(jsonString, RoomResponse[].class);
                                        roomList = Arrays.asList(array);
                                        loadgrideDate(false, true,categoryId);
                                    } else {
                                        Toast.makeText(RoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    OkHttpUtils
                            .post()
                            .url(Api.apiHost + Api.findRoom)
                            .addParams("categoryId", categoryId)
                            .addParams("longitude", longitude + "")
                            .addParams("latitude", latitude + "")
                            .addParams("city",city.trim())
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
                                        String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                                        RoomResponse[] array = MyUtils.getGson().fromJson(jsonString, RoomResponse[].class);
                                        roomList = Arrays.asList(array);
                                        loadgrideDate(false, true,categoryId);
                                    } else {
                                        Toast.makeText(RoomActivity.this, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    private boolean isGpsAble(LocationManager lm){
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)? true:false;
    }

    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }

}
