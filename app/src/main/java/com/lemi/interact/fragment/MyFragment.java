package com.lemi.interact.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.activity.LoginActivity;
import com.lemi.interact.activity.RechargeRecordActivity;
import com.lemi.interact.activity.RoomActivity;
import com.lemi.interact.activity.UserInfoActivity;
import com.lemi.interact.activity.WithdrawActivity;
import com.lemi.interact.activity.WithdrawRecordActivity;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.RoomResponse;
import com.lemi.interact.bean.User;
import com.lemi.interact.util.MyUtils;
import com.lemi.interact.view.MyImgView;
import com.lemi.interact.view.PersonalItemView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Arrays;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class MyFragment extends Fragment implements View.OnClickListener {

    private Button loginOut;

    private TextView userNickName;

    private MyImgView userPhoto;

    private PersonalItemView itemLiWu;

    private PersonalItemView itemMyLiWu;

    private PersonalItemView itemChongZhi;

    private PersonalItemView itemTiXian;

    private LinearLayout llUserinfo;

    private TextView moneyText;

    private Button goToTX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public MyFragment() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_out:
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivityForResult(intent, REQ_CODE_FOR_REGISTER);
                break;
            case R.id.item_liwu:
                Toast.makeText(getActivity(), "暂未开放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_my_liwu:
                Toast.makeText(getActivity(), "暂未开放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_chongzhi:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), RechargeRecordActivity.class);
                startActivityForResult(intent3, REQ_CODE_FOR_REGISTER);
                break;
            case R.id.item_tixian:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), WithdrawRecordActivity.class);
                startActivityForResult(intent2, REQ_CODE_FOR_REGISTER);
                break;
            case R.id.ll_userinfo:
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), UserInfoActivity.class);
                startActivityForResult(intent1, REQ_CODE_FOR_REGISTER);
                break;
            case R.id.go_to_tx:
                Intent intent4 = new Intent();
                intent4.setClass(getActivity(), WithdrawActivity.class);
                startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                break;

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginOut = view.findViewById(R.id.login_out);
        loginOut.setOnClickListener(this);

        userNickName = view.findViewById(R.id.user_nick_name);

        userPhoto = view.findViewById(R.id.user_photo);

        itemLiWu = view.findViewById(R.id.item_liwu);
        itemLiWu.setOnClickListener(this);

        itemMyLiWu = view.findViewById(R.id.item_my_liwu);
        itemMyLiWu.setOnClickListener(this);

        itemChongZhi = view.findViewById(R.id.item_chongzhi);
        itemChongZhi.setOnClickListener(this);

        itemTiXian = view.findViewById(R.id.item_tixian);
        itemTiXian.setOnClickListener(this);

        llUserinfo = view.findViewById(R.id.ll_userinfo);
        llUserinfo.setOnClickListener(this);

        moneyText = view.findViewById(R.id.money);

        goToTX = view.findViewById(R.id.go_to_tx);
        goToTX.setOnClickListener(this);
        init();

    }

    private void init() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findUserById)
                .addParams("userId", userId)
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
                            String jsonString = MyUtils.getGson().toJson(apiResult.getData());
                            User user = MyUtils.getGson().fromJson(jsonString, new TypeToken<User>() {
                            }.getType());
                            if (user.getNickName() != null) {
                                userNickName.setText(user.getNickName());
                            }

                            if (user.getPhoto() != null) {
                                userPhoto.setImageURL(user.getPhoto());
                            } else {
                                userPhoto.setImageResource(R.mipmap.defult_head);
                            }
                            if (user.getMoney()!=null){
                                moneyText.setText("￥" + user.getMoney());
                            }else {
                                moneyText.setText("￥0");
                            }
                        } else {
                            Toast.makeText(getActivity(), apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
