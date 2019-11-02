package com.lemi.interact.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.util.FastBlur;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class AddRoomActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout linearLayout;

    ImageButton addRoomBack;

    private String mcategoryId;

    Context context;

    Button addRoomBtn;

    EditText price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String categoryId=sharedPreferences.getString("categoryId","");
        mcategoryId = categoryId;

        linearLayout = findViewById(R.id.add_room_ll);
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

        init();
    }

    private void init(){
        addRoomBack = findViewById(R.id.add_room_back);
        addRoomBack.setOnClickListener(this);

        addRoomBtn = findViewById(R.id.add_room1_btn);
        addRoomBtn.setOnClickListener(this);

        price = findViewById(R.id.price);
        if (mcategoryId.equals("4")){
            price.setEnabled(false);
        }else {
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
                String userId = sharedPreferences.getString("userId", "");
                Integer isFree = 1;
                if (mcategoryId.equals("4")){
                    if (pri == null || "".equals(pri)){
                        isFree = 1;
                    }else if (Integer.parseInt(pri) == 0){
                        isFree = 1;
                    }else {
                        isFree = 2;
                    }
                }

                if (pri == null || "".equals(pri)){
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

                                    if (apiResult.getData()!=null){
                                        java.lang.reflect.Type roomType = new TypeToken<Room>() {
                                        }.getType();
                                        Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);

                                        String roomID = room.getNum();

                                        Intent intent = new Intent();
                                        intent.putExtra("roomId", roomID);
                                        intent.setClass(context, RoomInfoActivity.class);
                                        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                        finish();
                                        Toast.makeText(AddRoomActivity.this, "创建房间成功", Toast.LENGTH_SHORT).show();
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
}
