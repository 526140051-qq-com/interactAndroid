package com.lemi.interact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lemi.interact.activity.IndexActivity;
import com.lemi.interact.activity.LoginActivity;


public class MainActivity extends Activity{

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
            Intent intent = new Intent();
            intent.setClass(context, IndexActivity.class);
            startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
            finish();
        }

    }
}
