package com.lemi.interact;

import android.Manifest;
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

import java.util.List;

import io.rong.imlib.RongIMClient;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    public static final int REQ_CODE_FOR_REGISTER = 1;

    private static final int RC_CAMERA_AND_LOCATION = 100;

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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(this, "应用需要权限",
                    RC_CAMERA_AND_LOCATION, perms);

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        methodRequiresTwoPermission();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
