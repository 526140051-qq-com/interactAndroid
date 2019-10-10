package com.lemi.interact.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;


public class ContentAdapter extends FragmentPagerAdapter {
    private FragmentManager fragmentManager;
    private ArrayList<Fragment> list;

    public ContentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.fragmentManager = fm;
        this.list = list;

    }

    @Override//返回要显示的碎片
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override//返回要显示多少页
    public int getCount() {
        return list.size();
    }
}


