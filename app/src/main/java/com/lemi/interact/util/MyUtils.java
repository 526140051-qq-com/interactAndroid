package com.lemi.interact.util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.lemi.interact.R;
import com.lemi.interact.gson.DoubleDefault0Adapter;
import com.lemi.interact.gson.IntegerDefault0Adapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MyUtils {
    private static Gson gson;

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    static public Dialog createLoadingDialog(Context context, String msg) {

//        LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
//        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
//        // main.xml中的ImageView
//        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img_iv);
//        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
//        // 加载动画
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.load_animation);
//        // 使用ImageView显示动画
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
//        tipTextView.setText(msg);// 设置加载信息
//
//        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
//
//        loadingDialog.setCancelable(true);// 可以用“返回键”取消
//        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
//        return loadingDialog;

        return null;

    }

    public static void Msg(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void Msg(Context context, String str, boolean flog) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static String json2Str(JSONObject json) {
        Iterator<String> keys = json.keys();
        String back = "{";
        while (keys.hasNext()) {
            try {
                String key = keys.next();
                String value = json.getString(key);
                back += "\"" + key + "\":\"" + value + "\",";
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        back = back.substring(0, back.lastIndexOf(","));
        back += "}";
        return back;
    }

    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static void alertDialog(Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle("系统提示")
                .setMessage(msg)
                .setNegativeButton(
                        "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {

                            }
                        }).show();
    }

    public static void datePick(Context context, TextView view) {
        final TextView mView = view;
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                        String monthStr = "";
                        if ((arg2 + 1) < 10) {
                            monthStr = "0" + (arg2 + 1);
                        } else {
                            monthStr = (arg2 + 1) + "";
                        }
                        String day = "";
                        if (arg3 < 10) {
                            day = "0" + arg3;
                        } else {
                            day = arg3 + "";
                        }
                        mView.setText(arg1 + monthStr + day);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    public static String getCheckValueStr(Context context) {
        String checkValue = "";
        String json = context.getApplicationContext().getSharedPreferences("CheckValue", Context.MODE_PRIVATE).getString("json", "");
        checkValue = json;
        return checkValue;
    }

    public static String Encrypt(String time) throws NoSuchAlgorithmException {
        return getSHA(getMD5(time) + getMD5("chpcs@weijinhlw+"));
    }

    public static String getSHA(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("SHA-1");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密  
        return getString(m);
    }

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密  
        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }

    /**
     * 保存方法
     */
    public static String saveBitmap(Bitmap bm) {

        String picName = "a.png";
        Log.e("aaaaaa", "保存图片");
        File path = new File("/mnt/sdcard/namecard/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File f = new File("/mnt/sdcard/namecard/", picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("aaaaaa", "已经保存");
            return "/sdcard/namecard/" + picName;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //PgyCrashManager.reportCaughtException(context, e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //PgyCrashManager.reportCaughtException(context, e);
            return null;
        }

    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gb = new GsonBuilder();
            gb.registerTypeAdapter(Date.class, new DateSerializer()).setDateFormat(DateFormat.LONG)
                    .registerTypeAdapter(Date.class, new DateDeserializer()).setDateFormat(DateFormat.LONG)
                    .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                    .setDateFormat("yyyy-MM-dd");

            gson = gb.create();
            return gson;
        } else {
            return gson;
        }
    }


    static class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime());
        }
    }

    static class DateDeserializer implements JsonDeserializer<Date> {

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }
}
