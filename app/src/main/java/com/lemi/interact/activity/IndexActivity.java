package com.lemi.interact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;

import java.util.ArrayList;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lemi.interact.R;
import com.lemi.interact.adapter.ContentAdapter;
import com.lemi.interact.config.Seeting;
import com.lemi.interact.fragment.HomeFragment;
import com.lemi.interact.fragment.MyFragment;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class IndexActivity extends AppCompatActivity implements OnClickListener, ViewPager.OnPageChangeListener {
    private LinearLayout ll_home;
    private LinearLayout ll_my;
    private ImageView iv_home;
    private ImageView iv_my;
    private TextView tv_home;
    private TextView tv_my;
    private ViewPager viewPager;
    private ContentAdapter adapter;
    private ArrayList<Fragment> listFragment;
    String pageIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        pageIndex = intent.getStringExtra("pageIndex");
        initView();
        initEvent();
    }

    private void initEvent() {
        ll_home.setOnClickListener(this);
        ll_my.setOnClickListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    private void initView() {
        this.ll_home = (LinearLayout) findViewById(R.id.ll_home);
        this.ll_my = (LinearLayout) findViewById(R.id.ll_my);
        this.iv_home = (ImageView) findViewById(R.id.iv_home);
        this.iv_my = (ImageView) findViewById(R.id.iv_my);
        this.tv_home = (TextView) findViewById(R.id.tv_home);
        this.tv_my = (TextView) findViewById(R.id.tv_my);
        this.viewPager = (ViewPager) findViewById(R.id.vp_content);
        listFragment = new ArrayList<Fragment>(); //new一个List<Fragment>
        Fragment f1 = new HomeFragment();
        Fragment f2 = new MyFragment();
        listFragment.add(f1);
        listFragment.add(f2);
        FragmentManager fm = getSupportFragmentManager();
        this.adapter = new ContentAdapter(fm, listFragment);
        viewPager.setAdapter(adapter);
        if (pageIndex != null){
            restartBotton();
            iv_my.setImageResource(R.mipmap.icon_my_active);
            tv_my.setTextColor(0xff4c94ff);
            viewPager.setCurrentItem(1);
        }else {
            viewPager.setCurrentItem(0);
        }

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        restartBotton();
        switch (i) {
            case 0:
                iv_home.setImageResource(R.mipmap.icon_home_active);
                tv_home.setTextColor(0xff4c94ff);
                break;
            case 1:
                iv_my.setImageResource(R.mipmap.icon_my_active);
                tv_my.setTextColor(0xff4c94ff);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        restartBotton();
        switch (v.getId()) {
            case R.id.ll_home:
                iv_home.setImageResource(R.mipmap.icon_home_active);
                tv_home.setTextColor(0xff4c94ff);
                viewPager.setCurrentItem(0);
                break;
            case R.id.ll_my:
                iv_my.setImageResource(R.mipmap.icon_my_active);
                tv_my.setTextColor(0xff4c94ff);
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private void restartBotton() {
        iv_home.setImageResource(R.mipmap.icon_home);
        iv_my.setImageResource(R.mipmap.icon_my);
        tv_home.setTextColor(0xff8e9096);
        tv_my.setTextColor(0xff8e9096);
    }

}
