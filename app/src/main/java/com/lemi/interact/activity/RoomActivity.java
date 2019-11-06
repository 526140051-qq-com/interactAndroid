package com.lemi.interact.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.adapter.RecyclerViewGridAdapter;
import com.lemi.interact.adapter.SpacesItemDecoration;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.RoomResponse;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class RoomActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;

    private List<RoomResponse> roomList;

    private ImageButton addRoomBtn;

    private Context context;

    private LocationManager lm;

    private Double longitude;

    private Double latitude;

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
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null){
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String categoryId=sharedPreferences.getString("categoryId","");
        if (categoryId != null && !"".equals(categoryId)) {
            initData(categoryId);
        }

    }


    private void initData(final String categoryId) {
        recyclerView = findViewById(R.id.recyler_view);
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findRoom)
                .addParams("categoryId", categoryId)
                .addParams("longitude", longitude + "")
                .addParams("latitude", latitude + "")
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

        addRoomBtn = findViewById(R.id.add_room_btn);
        addRoomBtn.setOnClickListener(this);
    }


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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_room_btn:
                Intent intent = new Intent();
                intent.setClass(context, AddRoomActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                break;
        }
    }

    private boolean isGpsAble(LocationManager lm){
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)? true:false;
    }

    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }
}
