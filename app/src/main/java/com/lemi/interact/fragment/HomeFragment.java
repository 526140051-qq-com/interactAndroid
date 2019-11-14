package com.lemi.interact.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.activity.AdverInfoActivity;
import com.lemi.interact.activity.MemberActivity;
import com.lemi.interact.activity.RoomActivity;
import com.lemi.interact.adapter.AdverAdapter;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.Advertisement;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Arrays;
import java.util.List;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class HomeFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener {

    private VideoView videoView;

    private ImageButton roomCategory1;

    private ImageButton roomCategory2;

    private ImageButton roomCategory3;

    private ImageButton roomCategory4;

    private AdverAdapter adverAdapter;

    private ListView indexListView;

    private List<Advertisement> advertisementList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
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

    public HomeFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoView = view.findViewById(R.id.videoView);
        initVideo();
        roomCategory1 = view.findViewById(R.id.room_category_1);
        roomCategory1.setOnClickListener(this);
        roomCategory2 = view.findViewById(R.id.room_category_2);
        roomCategory2.setOnClickListener(this);
        roomCategory3 = view.findViewById(R.id.room_category_3);
        roomCategory3.setOnClickListener(this);
        roomCategory4 = view.findViewById(R.id.room_category_4);
        roomCategory4.setOnClickListener(this);

        indexListView = view.findViewById(R.id.adver_listView);
        indexListView.setOnItemClickListener(this);

        initAdver();


    }

    public void initAdver(){
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.selectAdvertisement)
                .addParams("state","1")
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
                            Advertisement[] array = MyUtils.getGson().fromJson(jsonString, Advertisement[].class);
                            advertisementList = Arrays.asList(array);
                            adverAdapter = new AdverAdapter(getActivity(), advertisementList);
                            indexListView.setAdapter(adverAdapter);
                            adverAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void initVideo() {
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findAppVideoUrl)
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
                            String path = "https://interact-app.oss-cn-beijing.aliyuncs.com/interact-app/image/376c2e8583fe4910840e29e36e5f10c1.mp4";
                            if (apiResult.getData() != null) {
                                path = apiResult.getData().toString();
                            }
                            videoView.setVideoPath(path);
//                            videoView.setMediaController(new MediaController(getActivity()));
                            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                public void onPrepared(MediaPlayer mp) {
                                    videoView.start();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (v.getId()) {
            case R.id.room_category_1:
                editor.putString("categoryId", 1 + "");
                editor.commit();
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), RoomActivity.class);
                startActivityForResult(intent1, REQ_CODE_FOR_REGISTER);
                break;

            case R.id.room_category_2:
                editor.putString("categoryId", 2 + "");
                editor.commit();
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), RoomActivity.class);
                startActivityForResult(intent2, REQ_CODE_FOR_REGISTER);
                break;

            case R.id.room_category_3:
                editor.putString("categoryId", 3 + "");
                editor.commit();
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), RoomActivity.class);
                startActivityForResult(intent3, REQ_CODE_FOR_REGISTER);
                break;

            case R.id.room_category_4:
                String userId = sharedPreferences.getString("userId", "");
                roomCategory4.setEnabled(false);
                OkHttpUtils
                        .post()
                        .url(Api.apiHost + Api.isChargeExpired)
                        .addParams("userId", userId)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                roomCategory4.setEnabled(true);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                java.lang.reflect.Type type = new TypeToken<ApiResult>() {
                                }.getType();
                                ApiResult apiResult = MyUtils.getGson().fromJson(response, type);
                                if (apiResult.getCode().intValue() == 0) {
                                    editor.putString("categoryId", 4 + "");
                                    editor.commit();
                                    double res = -1;
                                    if (apiResult.getData() == null) {
                                        res = 1;
                                    } else {
                                        res = Double.parseDouble(apiResult.getData().toString());
                                    }
                                    if ((int) res == 1) {
                                        Intent intent4 = new Intent();
                                        intent4.setClass(getActivity(), MemberActivity.class);
                                        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                    } else {
                                        Intent intent4 = new Intent();
                                        intent4.setClass(getActivity(), RoomActivity.class);
                                        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                    }

                                } else {
                                    Toast.makeText(getActivity(), apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                roomCategory4.setEnabled(true);
                            }
                        });


                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String adverId = (String) ((TextView) view.findViewById(R.id.adver_id)).getText();
        Intent intent = new Intent();
        intent.setClass(getActivity(), AdverInfoActivity.class);
        intent.putExtra("adverId",adverId);
        startActivityForResult(intent, REQ_CODE_FOR_REGISTER);

    }
}
