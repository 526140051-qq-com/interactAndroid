package com.lemi.interact.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lemi.interact.R;
import com.lemi.interact.bean.Advertisement;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AdverAdapter extends BaseAdapter {


    private Context context;
    private List<Advertisement> lists;

    public AdverAdapter(Context context, List<Advertisement> lists){
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_adver,null );
        Advertisement advertisement = lists.get(position);
        com.lemi.interact.view.BitmapImgView imageView = view.findViewById(R.id.img_item);
        TextView adverIdView = view.findViewById(R.id.adver_id);
        if (advertisement.getId()!=null){
            adverIdView.setText(advertisement.getId()+"");
        }
        if (advertisement.getImgs() != null && !"".equals(advertisement.getImgs())){
            imageView.setImageURL(advertisement.getImgs());
        }
        return view;
    }
}
