package com.lemi.interact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lemi.interact.R;
import com.lemi.interact.bean.Advertisement;
import com.lemi.interact.bean.Gift;
import com.lemi.interact.view.MyImgView;

import java.util.List;

public class GiftAdapter extends BaseAdapter {


    private Context context;
    private List<Gift> lists;

    public GiftAdapter(Context context, List<Gift> lists){
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gift,null );
        Gift gift = lists.get(position);
        MyImgView imageView = view.findViewById(R.id.img_liwu_item);
        TextView name = view.findViewById(R.id.gift_name);
        TextView giftIdView = view.findViewById(R.id.gift_id);
        TextView num = view.findViewById(R.id.gift_num);
        if (gift.getId()!=null){
            giftIdView.setText(gift.getId()+"");
        }
        if (gift.getPhoto() != null && !"".equals(gift.getPhoto())){
            imageView.setImageURL(gift.getPhoto());
        }
        if (gift.getName() != null && !"".equals(gift.getName())){
            name.setText(gift.getName());
        }
        if (gift.getCount() != null){
            num.setText("剩余 " + gift.getCount()+ " 件");
        }
        return view;
    }
}
