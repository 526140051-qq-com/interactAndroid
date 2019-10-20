package com.lemi.interact.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lemi.interact.R;
import com.lemi.interact.activity.IndexActivity;
import com.lemi.interact.activity.PersonalActivity;
import com.lemi.interact.bean.RoomResponse;
import com.lemi.interact.view.MyImgView;

import java.util.List;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;


public class RecyclerViewGridAdapter extends RecyclerView.Adapter<RecyclerViewGridAdapter.GridViewHolder> {
    private Context mContext;
    private String mCategoryId;
    private AppCompatActivity mAppCompatActivity;
    //泛型是RecylerView所需的Bean类
    private List<RoomResponse> mDateBeen;

    //构造方法，一般需要接受两个参数，上下文，集合对象（包含我们所需要的数据）
    public RecyclerViewGridAdapter(Context context, AppCompatActivity appCompatActivity, String categoryId, List<RoomResponse> dates) {
        mContext = context;
        mDateBeen = dates;
        mAppCompatActivity = appCompatActivity;
        mCategoryId = categoryId;
    }


    //创建ViewHolder也就是说创建出来一条,并把ViewHolder（item）返回出去
    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //转换一个View布局对象，决定了item的样子， 参数1：上下文 2. xml布局对象 3.为null
        View view = View.inflate(mContext, R.layout.activity_gridview, null);
        //创建一个ViewHolder对象
        final GridViewHolder gridViewHolder = new GridViewHolder(view);
        //把ViewHolder对象传出去
        gridViewHolder.provinceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = gridViewHolder.getAdapterPosition();
                RoomResponse roomResponse = mDateBeen.get(position);
                Integer createUserId = roomResponse.getCreateUserId();
                Integer roomId = roomResponse.getRoomId();
                Toast.makeText(v.getContext(), "你点击了项,roomId=" + roomId + ",createUserId=" + createUserId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("roomId", roomId + "");
                intent.putExtra("createUserId", createUserId + "");
                intent.putExtra("categoryId", mCategoryId);
                intent.setClass(mContext, PersonalActivity.class);
                mAppCompatActivity.startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                mAppCompatActivity.finish();
            }
        });
        return gridViewHolder;
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position) {
        //从集合里拿对应item的数据对象
        RoomResponse dateBean = mDateBeen.get(position);
        //给holder里面的控件对象设置数据

        holder.setData(dateBean);
    }

    @Override
    public int getItemCount() {
        //数据不为null，有几条数据就显示几条数据
        if (mDateBeen != null && mDateBeen.size() > 0) {
            return mDateBeen.size();
        }
        return 0;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        private final TextView itemRoomNum;
        private final MyImgView itemPhoto;
        private final TextView itemNickName;
        private final ImageView itemGender;
        private final MyImgView itemJoinPhoto;
        private final TextView itemJoinNickName;
        private final ImageView itemJoinGender;
        View provinceView;


        public GridViewHolder(View itemView) {
            super(itemView);
            provinceView = itemView;
            itemRoomNum = itemView.findViewById(R.id.item_room_num);
            itemPhoto = itemView.findViewById(R.id.item_photo);
            itemNickName = itemView.findViewById(R.id.item_nick_name);
            itemGender = itemView.findViewById(R.id.item_gender);
            itemJoinPhoto = itemView.findViewById(R.id.item_join_photo);
            itemJoinNickName = itemView.findViewById(R.id.item_join_nick_name);
            itemJoinGender = itemView.findViewById(R.id.item_join_gender);
        }

        public void setData(RoomResponse data) {
            if (data.getNum() != null) {
                itemRoomNum.setText("房间号：" + data.getNum());
            }

            if (data.getPhoto() != null && !"".equals(data.getPhoto())) {
                itemPhoto.setImageURL(data.getPhoto());
            } else {
                itemPhoto.setImageResource(R.mipmap.defult_head);
            }

            if (data.getNickName() != null && !"".equals(data.getNickName())) {
                itemNickName.setText(data.getNickName());
            } else {
                itemNickName.setText("");
            }

            if (data.getGender() != null) {
                if (data.getGender().intValue() == 1) {
                    itemGender.setImageResource(R.mipmap.boy);
                } else {
                    itemGender.setImageResource(R.mipmap.girl);
                }
            }

            if (data.getJoinPhoto() != null && !"".equals(data.getJoinPhoto())) {
                itemJoinPhoto.setImageURL(data.getJoinPhoto());
            } else {
                itemJoinPhoto.setImageResource(R.mipmap.defult_head);
            }

            if (data.getJoinNickName() != null && !"".equals(data.getJoinNickName())) {
                itemJoinNickName.setText(data.getJoinNickName());
            } else {
                itemJoinNickName.setText("");
            }

            if (data.getJoinGender() != null) {
                if (data.getJoinGender().intValue() == 1) {
                    itemJoinGender.setImageResource(R.mipmap.boy);
                } else {
                    itemJoinGender.setImageResource(R.mipmap.girl);
                }
            }
        }
    }
}
