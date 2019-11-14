package com.lemi.interact.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lemi.interact.R;
import com.lemi.interact.bean.RegionProvinceBean;

import java.util.ArrayList;
import java.util.List;
public class AddressPickerDialog{
//public class AddressPickerDialog extends Dialog implements View.OnClickListener {
//
//    private WheelView wvProvince, wvCity, wvArea;
//    //弹出框的标题
//    private String title;
//
//    private Context mContext;
//
//    private List<RegionProvinceBean> provinceBeans = null;
//
//    AddressPickerDialog(Context context, String title, AddressPickerDialog.onAreaPickerDialogClickListener listener) {
//        // 在构造方法里, 传入主题
//        super(context, R.style.BottomDialogStyle);
//        mContext = context;
//        // 拿到Dialog的Window, 修改Window的属性
//        Window window = getWindow();
//        assert window != null;
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        //获取当前屏幕的宽高
//        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//
//        // 获取Window的LayoutParams
//        WindowManager.LayoutParams attributes = window.getAttributes();
//        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
//        attributes.height = dm.heightPixels / 3;//默认显示高度为屏幕的三分之一
//        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//        attributes.dimAmount = 0.3f;
//
//        // 一定要重新设置, 才能生效
//        window.setAttributes(attributes);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//        this.title = title;
//        this.listener = listener;
//    }
//
//    @SuppressLint("HandlerLeak")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_wheelview_address);
//
//        initView();
//        initData();
//    }
//
//    private void initView() {
//        TextView tvAddressTitle = findViewById(R.id.tv_addresspicker_title);
//        wvProvince = findViewById(R.id.wheelv_province);
//        wvCity = findViewById(R.id.wheelv_city);
//        wvArea = findViewById(R.id.wheelv_area);
//
//        findViewById(R.id.tv_addresspicker_confirm).setOnClickListener(this);
//        findViewById(R.id.tv_addresspicker_exit).setOnClickListener(this);
//
//        if (!TextUtils.isEmpty(title)) tvAddressTitle.setText(title);
//    }
//
//    private RegionProvinceBean curPBean;
//    private RegionCityBean curCBean;
//    private RegionAreaBean curABean;
//
//    private void initData() {
//        String regionStr = getLocalJson();
//        Gson gson = new Gson();
//        RegionBean regionBean = gson.fromJson(regionStr, RegionBean.class);
//        if (regionBean == null) {
//            return;
//        }
//        provinceBeans = regionBean.getData();
//        curPBean = provinceBeans.get(0);
//        wvProvince.setCyclic(false);
//        wvCity.setCyclic(false);
//        wvArea.setCyclic(false);
//
//        ArrayList<String> pList = new ArrayList<>();
//        final ArrayList<String> cList = new ArrayList<>();
//        final ArrayList<String> aList = new ArrayList<>();
//
//        curPBean = provinceBeans.get(0);
//        for (RegionProvinceBean p : provinceBeans) {
//            pList.add(p.getAreaname());
//        }
//        wvProvince.setAdapter(new ArrayWheelAdapter<>(pList));
//        wvProvince.setCurrentItem(0);
//
//        curCBean = curPBean.getSubarea().get(0);
//        for (RegionCityBean c : curPBean.getSubarea()) {
//            cList.add(c.getAreaname());
//        }
//        wvCity.setAdapter(new ArrayWheelAdapter<>(cList));
//        wvCity.setCurrentItem(0);
//
//        curABean = curCBean.getSubarea().get(0);
//        for (RegionAreaBean a : curCBean.getSubarea()) {
//            aList.add(a.getAreaname());
//        }
//        wvArea.setAdapter(new ArrayWheelAdapter<>(aList));
//        wvArea.setCurrentItem(0);
//
//        wvProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                curPBean = provinceBeans.get(index);
//
//                //联动第二级
//                cList.clear();
//                ArrayList<RegionCityBean> cBeans = curPBean.getSubarea();
//                curCBean = cBeans.get(0);
//                for (RegionCityBean c : cBeans) {
//                    cList.add(c.getAreaname());
//                }
//                wvCity.setAdapter(new ArrayWheelAdapter<>(cList));
//                wvCity.setCurrentItem(0);
//                //联动第三级
//                aList.clear();
//                ArrayList<RegionAreaBean> aBeans = curCBean.getSubarea();
//                curABean = aBeans.get(0);
//                for (RegionAreaBean a : aBeans) {
//                    aList.add(a.getAreaname());
//                }
//                wvArea.setAdapter(new ArrayWheelAdapter<>(aList));
//                wvArea.setCurrentItem(0);
//            }
//        });
//        wvCity.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                curCBean = curPBean.getSubarea().get(index);
//
//                //联动第三级
//                aList.clear();
//                ArrayList<RegionAreaBean> aBeans = curCBean.getSubarea();
//                curABean = aBeans.get(0);
//                for (RegionAreaBean a : aBeans) {
//                    aList.add(a.getAreaname());
//                }
//                wvArea.setAdapter(new ArrayWheelAdapter<>(aList));
//                wvArea.setCurrentItem(0);
//            }
//        });
//        wvArea.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                curABean = curCBean.getSubarea().get(index);
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View v) {
//        RegionChoosedBean bean = new RegionChoosedBean();
//        switch (v.getId()) {
//            case R.id.tv_addresspicker_confirm:
//                bean.setEmpty(false);
//                bean.setpCode(curPBean.getAreacode());
//                bean.setpName(curPBean.getAreaname());
//                bean.setcCode(curCBean.getAreacode());
//                bean.setcName(curCBean.getAreaname());
//                bean.setaCode(curABean.getAreacode());
//                bean.setaName(curABean.getAreaname());
//                listener.onChooseClick(bean);
//                break;
//            case R.id.tv_addresspicker_exit:
//                bean.setEmpty(true);
//                listener.onChooseClick(bean);
//                break;
//        }
//    }
//
//    private AddressPickerDialog.onAreaPickerDialogClickListener listener;
//
//    public interface onAreaPickerDialogClickListener {
//        void onChooseClick(RegionChoosedBean bean);
//    }
//
//    /**
//     * 获取本地json文件
//     *
//     * @return 本地json内容的字符串
//     */
//    private String getLocalJson() {
//        StringBuilder builder = new StringBuilder();
//        try {
//            AssetManager assetManager = mContext.getAssets(); // 获得assets资源管理器(assets中的文件无法直接访问,可以使用AssetManager访问)
//            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("region.json"), "UTF-8");
//            BufferedReader br = new BufferedReader(inputStreamReader);
//            String line;
//            while ((line = br.readLine()) != null) {
//                builder.append(line);
//            }
//            br.close();
//            inputStreamReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//    }
}