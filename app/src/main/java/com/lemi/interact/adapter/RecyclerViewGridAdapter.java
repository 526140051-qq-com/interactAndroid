package com.lemi.interact.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.activity.AddRoomActivity;
import com.lemi.interact.activity.PersonalActivity;
import com.lemi.interact.activity.RoomActivity;
import com.lemi.interact.activity.RoomInfoActivity;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.bean.RoomResponse;
import com.lemi.interact.config.Seeting;
import com.lemi.interact.util.MainHandler;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.view.MyImgView;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.List;

import io.rong.imlib.RongIMClient;

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
//        gridViewHolder.provinceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = gridViewHolder.getAdapterPosition();
//                RoomResponse roomResponse = mDateBeen.get(position);
//                final Integer roomId = roomResponse.getRoomId();
//                switch (v.getId()){
//                    case R.id.item_photo:
//                        getRoomById(roomId);
//                        break;
//                    case R.id.item_join_photo:
//                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
//                        final String userId = sharedPreferences.getString("userId", "");
//                        OkHttpUtils
//                                .post()
//                                .url(Api.apiHost + Api.payForRoom)
//                                .addParams("roomId", roomId + "")
//                                .addParams("userId", userId)
//                                .build()
//                                .execute(new StringCallback() {
//                                    @Override
//                                    public void onError(okhttp3.Call call, Exception e, int id) {
//                                    }
//
//                                    @Override
//                                    public void onResponse(String response, int id) {
//                                        java.lang.reflect.Type type = new TypeToken<ApiResult>() {
//                                        }.getType();
//                                        ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
//                                        if (apiResult.getCode().intValue() == 0) {
//
//                                            if (apiResult.getData() != null) {
//                                                IWXAPI api = WXAPIFactory.createWXAPI(mContext, "wxe3d1f34f56595a6e");
//                                                try {
//                                                    JSONObject jsonObject = new JSONObject(apiResult.getData().toString());
//
//                                                    PayReq req = new PayReq();
//                                                    req.appId = jsonObject.getString("appid");
//                                                    req.partnerId = jsonObject.getString("partnerid");
//                                                    req.prepayId = jsonObject.getString("prepayid");
//                                                    req.nonceStr = jsonObject.getString("noncestr");
//                                                    req.timeStamp = jsonObject.getString("timestamp");
//                                                    req.packageValue = jsonObject.getString("package");
//                                                    req.sign = jsonObject.getString("sign");
//                                                    api.sendReq(req);
//                                                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                    editor.putString("pay_type", "room");
//                                                    editor.putString("pay_room_id",roomId + "");
//                                                    editor.commit();
//                                                    Toast.makeText(mContext, "发起支付成功", Toast.LENGTH_SHORT).show();
//                                                } catch (Exception e) {
//                                                    Toast.makeText(mContext, "发起支付失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//
//                                        } else if (apiResult.getCode().intValue() == 2) {
//                                            if (apiResult.getData() != null) {
//                                                java.lang.reflect.Type roomType = new TypeToken<Room>() {
//                                                }.getType();
//                                                Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
//                                                String roomID = room.getNum();
//                                                connectRong(roomID, userId);
//                                            }
//                                        } else {
//                                            Toast.makeText(mContext, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//                                });
//                        break;
//                }
//
//            }
//        });
        return gridViewHolder;
    }


    private void connectRong(final String roomId, final String userId) {
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.getToken)
                .addParams("userId", userId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                        }.getType();
                        ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                        if (apiResult.getCode().intValue() == 0) {
                            String token = apiResult.getData().toString();
                            connect(roomId, token);
                        } else {
                            Toast.makeText(mContext, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void connect(final String roomId, final String token) {
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {

                RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                    }

                    @Override
                    public void onSuccess(String userid) {
                        Intent intent = new Intent();
                        intent.putExtra("roomId", roomId);
                        intent.setClass(mContext, RoomInfoActivity.class);
                        mAppCompatActivity.startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                        mAppCompatActivity.finish();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        System.out.println(errorCode.getMessage());
                    }
                });
            }
        });
    }

    private void getRoomById(Integer roomId) {
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findRoomById)
                .addParams("roomId", roomId + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                        }.getType();
                        ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                        if (apiResult.getCode().intValue() == 0) {

                            if (apiResult.getData() != null && !apiResult.getData().toString().equals("")) {

                                java.lang.reflect.Type roomType = new TypeToken<Room>() {
                                }.getType();
                                Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
                                String num = room.getNum();
                                if (Seeting.list.indexOf(num) != -1) {
                                    if (room.getCreateUserId() != null) {
                                        Intent intent = new Intent();
                                        intent.putExtra("roomId", room.getId() + "");
                                        intent.putExtra("createUserId", room.getCreateUserId() + "");
                                        intent.putExtra("categoryId", mCategoryId);
                                        intent.setClass(mContext, PersonalActivity.class);
                                        mAppCompatActivity.startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                    } else {
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, AddRoomActivity.class);
                                        intent.putExtra("roomId", room.getId() + "");
                                        mAppCompatActivity.startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                    }
                                } else {
                                    Intent intent = new Intent();
                                    intent.putExtra("roomId", room.getId() + "");
                                    intent.putExtra("createUserId", room.getCreateUserId() + "");
                                    intent.putExtra("categoryId", mCategoryId);
                                    intent.setClass(mContext, PersonalActivity.class);
                                    mAppCompatActivity.startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                                }
                            }else {
                                Toast.makeText(mContext, "房间已解散", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
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

    public class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView itemRoomNum;
        private final MyImgView itemPhoto;
        private final TextView itemNickName;
        private final ImageView itemGender;
        private final MyImgView itemJoinPhoto;
        private final TextView itemJoinNickName;
        private final ImageView itemJoinGender;
        private final TextView distance;
        private final TextView city;
        private final ImageView linkIcon;

        View provinceView;


        public GridViewHolder(View itemView) {
            super(itemView);
            provinceView = itemView;
            itemRoomNum = itemView.findViewById(R.id.item_room_num);
            itemPhoto = itemView.findViewById(R.id.item_photo);
            itemPhoto.setOnClickListener(this);
            itemNickName = itemView.findViewById(R.id.item_nick_name);
            itemGender = itemView.findViewById(R.id.item_gender);
            itemJoinPhoto = itemView.findViewById(R.id.item_join_photo);
            itemJoinPhoto.setOnClickListener(this);
            itemJoinNickName = itemView.findViewById(R.id.item_join_nick_name);
            itemJoinGender = itemView.findViewById(R.id.item_join_gender);
            distance = itemView.findViewById(R.id.distance);
            city = itemView.findViewById(R.id.city);
            linkIcon = itemView.findViewById(R.id.link_icon);
        }

        public void setData(RoomResponse data) {
            if (data.getNum() != null) {
                itemRoomNum.setText("房间号：" + data.getNum());
            }

            if (data.getDistance() != null) {
                distance.setText("距离" + Math.ceil(data.getDistance() / 1000) + "km");
            } else {
                distance.setText("距离0km");
            }
            if (data.getCity() != null) {
                city.setText(data.getCity());
            } else {

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

            if (data.getCreateUserId() != null && data.getJoinNickName() != null && !"".equals(data.getJoinNickName())) {
                linkIcon.setImageResource(R.mipmap.yilianje);
            } else {
                linkIcon.setImageResource(R.mipmap.lianjie);
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

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            RoomResponse roomResponse = mDateBeen.get(position);
            final Integer roomId = roomResponse.getRoomId();
            switch (view.getId()) {
                case R.id.item_photo:
                    itemPhoto.setEnabled(false);
                    getRoomById(roomId);
                    itemPhoto.setEnabled(true);
                    break;
                case R.id.item_join_photo:
                    itemJoinPhoto.setEnabled(false);
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
                    final String userId = sharedPreferences.getString("userId", "");
                    OkHttpUtils
                            .post()
                            .url(Api.apiHost + Api.payForRoom)
                            .addParams("roomId", roomId + "")
                            .addParams("userId", userId)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(okhttp3.Call call, Exception e, int id) {
                                    itemJoinPhoto.setEnabled(true);
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                                    }.getType();
                                    ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                                    if (apiResult.getCode().intValue() == 0) {

                                        if (apiResult.getData() != null) {
                                            IWXAPI api = WXAPIFactory.createWXAPI(mContext, "wxe3d1f34f56595a6e");
                                            try {
                                                JSONObject jsonObject = new JSONObject(apiResult.getData().toString());

                                                PayReq req = new PayReq();
                                                req.appId = jsonObject.getString("appid");
                                                req.partnerId = jsonObject.getString("partnerid");
                                                req.prepayId = jsonObject.getString("prepayid");
                                                req.nonceStr = jsonObject.getString("noncestr");
                                                req.timeStamp = jsonObject.getString("timestamp");
                                                req.packageValue = jsonObject.getString("package");
                                                req.sign = jsonObject.getString("sign");
                                                api.sendReq(req);
                                                String out_trade_no = jsonObject.getString("out_trade_no");
                                                String roomNum = jsonObject.getString("room_num");
                                                SharedPreferences sharedPreferences = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("pay_type","room");
                                                editor.putString("out_trade_no",out_trade_no);
                                                editor.putString("room_num",roomNum);
                                                editor.commit();
                                                Toast.makeText(mContext, "发起支付成功", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                Toast.makeText(mContext, "发起支付失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } else if (apiResult.getCode().intValue() == 2) {
                                        if (apiResult.getData() != null) {
                                            java.lang.reflect.Type roomType = new TypeToken<Room>() {
                                            }.getType();
                                            Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
                                            String roomID = room.getNum();
                                            connectRong(roomID, userId);
                                        }
                                    } else {
                                        Toast.makeText(mContext, apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    itemJoinPhoto.setEnabled(true);
                                }
                            });
                    break;
            }
        }
    }
}
