package com.lemi.interact.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lemi.interact.R;
import com.lemi.interact.util.FastBlur;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class AddRoomActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout linearLayout;

    ImageButton addRoomBack;

    private String mcategoryId;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;
        Intent intent = getIntent();
        String categoryId = intent.getStringExtra("categoryId");
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_room_back:
                Intent intent = new Intent();
                intent.setClass(context, RoomActivity.class);
                intent.putExtra("categoryId", mcategoryId);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                finish();
                break;
        }
    }
}
