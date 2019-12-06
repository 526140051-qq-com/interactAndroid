package com.lemi.interact.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.dialog.ActionSheetDialog;
import com.lemi.interact.util.GetPathFromUri4kitkat;

import java.io.File;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class WithdrawActivity extends Activity implements View.OnClickListener{

    private WebView webView;

    private WebSettings webSettings;

    private ImageView back;

    private Context context;

    public static ValueCallback mFilePathCallback;

    private static final int REQUEST_CODE_TAKE_PICETURE = 11;

    private static final int REQUEST_CODE_PICK_PHOTO = 12;

    private File picturefile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_withdraw);
        context = this;
        init();
    }

    private void init() {
        webView = (WebView) findViewById(R.id.draw_web);

        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(getApplicationContext().getCacheDir().getAbsolutePath());
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + ";userId=" + userId);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(2);
        }
        webView.addJavascriptInterface(new JsInteration(), "android");
        webView.setWebViewClient(new MyWebViewClient());
        String url = Api.h5Host + Api.withdraw;
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {//5.0+
                showDialog();
                mFilePathCallback = filePathCallback;
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {// android 系统版本>4.1.1
                showDialog();
                mFilePathCallback = uploadMsg;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {//android 系统版本<3.0
                showDialog();
                mFilePathCallback = uploadMsg;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {//android 系统版本3.0+
                showDialog();
                mFilePathCallback = uploadMsg;
            }
        });
        back = (ImageView) findViewById(R.id.draw_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.draw_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("pageIndex", 1 + "");
                    intent.setClass(this, IndexActivity.class);
                    startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                    finish();
                    break;
                }
        }
    }

    private void showDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(this).builder().addSheetItem("拍摄", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(context)
                                .setTitle("权限提醒")
                                .setMessage("需要开启权限")
                                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3000);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setCancelable(false).show();
                    } else {
                        takeForPicture();
                    }
                } else {
                    takeForPicture();
                }

            }
        }).addSheetItem("从手机相册选择", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(context)
                                .setTitle("权限提醒")
                                .setMessage("需要开启权限")
                                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3000);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).setCancelable(false).show();
                    } else {
                        takeForPhoto();
                    }
                } else {
                    takeForPhoto();
                }
            }
        }).setCancelable(false).setCanceledOnTouchOutside(false);

        dialog.show();
        //设置点击“取消”按钮监听，目的取消mFilePathCallback回调，可以重复调起弹窗
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelFilePathCallback();
            }
        });
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO);

    }

    /**
     * 调用相机
     */
    private void takeForPicture() {
        File pFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyPictures");
        if (!pFile.exists()) {
            pFile.mkdirs();
        }
        picturefile = new File(pFile + File.separator + "IvMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
            Uri contentUri = FileProvider.getUriForFile(context, getResources().getString(R.string.filepath), picturefile);
            grantUriPermission(getPackageName(),contentUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {//7.0以下
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
        }
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICETURE);
    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PICETURE:
                takePictureResult(resultCode);
                break;

            case REQUEST_CODE_PICK_PHOTO:
                takePhotoResult(resultCode, data);
                break;
        }
    }

    private void takePictureResult(int resultCode) {
        if (mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.fromFile(picturefile);
                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }

    }

    private void takePhotoResult(int resultCode, Intent data) {
        if (mFilePathCallback != null){
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }

            }else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
        }
    }

    public class JsInteration {
        @JavascriptInterface
        public String toWXPay(Integer chargeId) {

            return "";
        }
    }
}
