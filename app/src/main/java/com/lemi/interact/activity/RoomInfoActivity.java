package com.lemi.interact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lemi.interact.R;
import com.lemi.interact.api.Api;
import com.lemi.interact.bean.ApiResult;
import com.lemi.interact.bean.Room;
import com.lemi.interact.util.GPUImageBeautyFilter;
import com.lemi.interact.util.MyUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import cn.rongcloud.rtc.RTCErrorCode;
import cn.rongcloud.rtc.RongRTCEngine;
import cn.rongcloud.rtc.callback.JoinRoomUICallBack;
import cn.rongcloud.rtc.callback.RongRTCResultUICallBack;
import cn.rongcloud.rtc.engine.view.RongRTCVideoView;
import cn.rongcloud.rtc.events.ILocalAudioPCMBufferListener;
import cn.rongcloud.rtc.events.ILocalVideoFrameListener;
import cn.rongcloud.rtc.events.IRemoteAudioPCMBufferListener;
import cn.rongcloud.rtc.events.RTCAudioFrame;
import cn.rongcloud.rtc.events.RTCVideoFrame;
import cn.rongcloud.rtc.events.RongRTCEventsListener;
import cn.rongcloud.rtc.room.RongRTCRoom;
import cn.rongcloud.rtc.stream.local.RongRTCCapture;
import cn.rongcloud.rtc.stream.local.RongRTCLocalSourceManager;
import cn.rongcloud.rtc.stream.remote.RongRTCAVInputStream;
import cn.rongcloud.rtc.user.RongRTCLocalUser;
import cn.rongcloud.rtc.user.RongRTCRemoteUser;
import cn.rongcloud.rtc.utils.debug.RTCDevice;
import io.rong.imlib.model.Message;
import cn.rongcloud.rtc.stream.MediaType;

import static com.lemi.interact.MainActivity.REQ_CODE_FOR_REGISTER;

public class RoomInfoActivity extends Activity implements RongRTCEventsListener, View.OnClickListener, ILocalVideoFrameListener, ILocalAudioPCMBufferListener, IRemoteAudioPCMBufferListener {

    private RongRTCVideoView local;
    private LinearLayout remotes;
    private RongRTCRoom mRongRTCRoom;
    private RongRTCLocalUser mLocalUser;
    private String mRoomId;
    private Button button;
    private Context context;
    private FrameLayout localContainer;
    private ImageButton levelRoom;
    private ImageButton qhRoom;
    private ImageButton gbRoom;
    private GPUImageBeautyFilter beautyFilter;
    private boolean qianhou_flag = false;
    private boolean guanbi_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_room_info);
        Intent intent = getIntent();
        mRoomId = intent.getStringExtra("roomId");
        context = this;
        initView();

    }

    private void initView() {
        local = RongRTCEngine.getInstance().createVideoView(this);
        local.setOnClickListener(this);
        localContainer = (FrameLayout) findViewById(R.id.local_container);
        localContainer.addView(local);
        remotes = (LinearLayout) findViewById(R.id.remotes);
        button = (Button) findViewById(R.id.finish);
        button.setVisibility(View.GONE);
        button.setOnClickListener(this);

        levelRoom = (ImageButton) findViewById(R.id.live_room);
        levelRoom.setOnClickListener(this);
        qhRoom = findViewById(R.id.qh_room);
        qhRoom.setOnClickListener(this);
        gbRoom = findViewById(R.id.gb_room);
        gbRoom.setOnClickListener(this);
        joinRoom();
    }

    /**
     * 加入房间
     */
    private void joinRoom() {
        RongRTCEngine.getInstance().joinRoom(mRoomId, new JoinRoomUICallBack() {
            @Override
            protected void onUiSuccess(RongRTCRoom rongRTCRoom) {
                mRongRTCRoom = rongRTCRoom;
                mLocalUser = rongRTCRoom.getLocalUser();
                RongRTCCapture.getInstance().setRongRTCVideoView(local); //设置本地预览视图
                RongRTCCapture.getInstance().startCameraCapture();       //开始采集数据
                setEventListener();                                      //设置监听
                addRemoteUsersView();
                subscribeAll();                                          //订阅资源
                publishDefaultStream();                                  //发布资源
            }

            @Override
            protected void onUiFailed(RTCErrorCode rtcErrorCode) {
            }
        });

        RongRTCCapture.getInstance().setLocalVideoFrameListener(RTCDevice.getInstance().isTexture(), this);
        RongRTCCapture.getInstance().setLocalAudioPCMBufferListener(this);
        RongRTCCapture.getInstance().setRemoteAudioPCMBufferListener(this);

        RongRTCCapture.getInstance().setEnableSpeakerphone(true);
        RongRTCCapture.getInstance().startCameraCapture();
    }

    /**
     * 注册监听
     */
    private void setEventListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.registerEventsListener(this);
        }
    }

    private void removeListener() {
        if (mRongRTCRoom != null) {
            mRongRTCRoom.unRegisterEventsListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.finish) {
            quitRoom();
            finish();
        } else if (view instanceof RongRTCVideoView) {
            int index = -1;
            for (int i = 0; i < remotes.getChildCount(); i++) {
                RongRTCVideoView videoView = (RongRTCVideoView) remotes.getChildAt(i);
                if (videoView == view) {
                    index = i;
                }
            }
            if (index != -1) {
                RongRTCVideoView big = (RongRTCVideoView) localContainer.getChildAt(0);
                big.setZOrderOnTop(true);
                big.setZOrderMediaOverlay(true);
                localContainer.removeViewAt(0);
                remotes.addView(big, index, new LinearLayout.LayoutParams(remotes.getWidth(), remotes.getHeight()));
                remotes.removeView(view);
                ((RongRTCVideoView) view).setZOrderOnTop(false);
                ((RongRTCVideoView) view).setZOrderMediaOverlay(false);
                localContainer.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else if (view.getId() == R.id.live_room) {
            SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            final String userId = sharedPreferences.getString("userId", "");
            OkHttpUtils
                    .post()
                    .url(Api.apiHost + Api.findRoomByNum)
                    .addParams("num", mRoomId)
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
                                    java.lang.reflect.Type roomType = new TypeToken<Room>(){}.getType();
                                    Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
                                    if (room.getCreateUserId() != null && room.getCreateUserId().toString().equals(userId)) {
                                        String str = "TA在热忱陪着你，确定要离开房间吗？若离开，本次收益将退回。";
                                        new AlertDialog.Builder(context).setTitle("提醒")
                                                .setMessage(str)
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .setPositiveButton("确认离开", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        removeListener();
                                                        quitRoom();
                                                        Intent intent4 = new Intent();
                                                        intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                                        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                                        finish();
                                                    }
                                                })
                                                .setNegativeButton("暂不离开", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    } else if (room.getJoinUserId() != null && room.getJoinUserId().toString().equals(userId)) {
                                        String str = "离开房间您将失去Ta的陪伴，确定要残忍离开吗？";
                                        new AlertDialog.Builder(context).setTitle("提醒")
                                                .setMessage(str)
                                                .setIcon(android.R.drawable.ic_dialog_info)
                                                .setPositiveButton("残忍离开", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        removeListener();
                                                        quitRoom();
                                                        Intent intent4 = new Intent();
                                                        intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                                        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                                        finish();

                                                    }
                                                })
                                                .setNegativeButton("继续陪伴", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    }else {
                                        removeListener();
                                        quit();
                                        Intent intent4 = new Intent();
                                        intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                        finish();
                                    }
                                } else {
                                    removeListener();
                                    quit();
                                    Intent intent4 = new Intent();
                                    intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                    startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                    finish();
                                }
                            } else {
                                removeListener();
                                quit();
                                Intent intent4 = new Intent();
                                intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                finish();
                            }
                        }
                    });
        } else if (view.getId() == R.id.qh_room) {
            if (qianhou_flag) {
                qhRoom.setImageDrawable(getResources().getDrawable(R.mipmap.qianhou_active));
            } else {
                qhRoom.setImageDrawable(getResources().getDrawable(R.mipmap.qianhou));
            }
            qianhou_flag = !qianhou_flag;
            RongRTCCapture.getInstance().switchCamera();
        } else if (view.getId() == R.id.gb_room) {
            if (guanbi_flag) {
                gbRoom.setImageDrawable(getResources().getDrawable(R.mipmap.guanbi_active));
            } else {
                gbRoom.setImageDrawable(getResources().getDrawable(R.mipmap.guanbi));
            }
            guanbi_flag = !guanbi_flag;
            RongRTCCapture.getInstance().muteLocalVideo(guanbi_flag);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RongRTCCapture.getInstance().stopCameraCapture();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
        quitRoom();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RongRTCLocalSourceManager.getInstance().startCapture();
    }

    private void addRemoteUsersView() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                for (RongRTCAVInputStream inputStream : remoteUser.getRemoteAVStreams()) {
                    if (inputStream.getMediaType() == MediaType.VIDEO) {
                        inputStream.setRongRTCVideoView(getNewVideoView());
                    }
                }
            }
        }
    }

    /**
     * 订阅所有当前在房间发布资源的用户
     */
    private void subscribeAll() {
        if (mRongRTCRoom != null) {
            for (RongRTCRemoteUser remoteUser : mRongRTCRoom.getRemoteUsers().values()) {
                remoteUser.subscribeAvStream(remoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
                    @Override
                    public void onUiSuccess() {
                        //Toast.makeText(RoomInfoActivity.this, "订阅资源成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUiFailed(RTCErrorCode rtcErrorCode) {
                        //Toast.makeText(RoomInfoActivity.this, "订阅资源失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * 发布资源
     */
    private void publishDefaultStream() {
        if (mLocalUser != null) {
            mLocalUser.publishDefaultAVStream(new RongRTCResultUICallBack() {
                @Override
                public void onUiSuccess() {
                    //Toast.makeText(RoomInfoActivity.this, "发布资源成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onUiFailed(RTCErrorCode rtcErrorCode) {
                    //Toast.makeText(RoomInfoActivity.this, "发布资源失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private RongRTCVideoView getNewVideoView() {
        RongRTCVideoView videoView = RongRTCEngine.getInstance().createVideoView(this);
        videoView.setZOrderOnTop(true);
        videoView.setZOrderMediaOverlay(true);
        videoView.setOnClickListener(this);
        remotes.addView(videoView, new LinearLayout.LayoutParams(remotes.getWidth(), remotes.getHeight()));
        remotes.bringToFront();
        return videoView;
    }

    @Override
    public void onRemoteUserPublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            if (inputStream.getMediaType() == MediaType.VIDEO) {
                RongRTCVideoView videoView = RongRTCEngine.getInstance().createVideoView(this);
                videoView.setZOrderOnTop(true);
                videoView.setZOrderMediaOverlay(true);
                videoView.setOnClickListener(this);
                remotes.addView(videoView, new LinearLayout.LayoutParams(remotes.getWidth(), remotes.getHeight()));
                remotes.bringToFront();
                inputStream.setRongRTCVideoView(videoView);
            }
        }
        rongRTCRemoteUser.subscribeAVStream(rongRTCRemoteUser.getRemoteAVStreams(), new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                //Toast.makeText(RoomInfoActivity.this, "订阅成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                //Toast.makeText(RoomInfoActivity.this, "订阅失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRemoteUserAudioStreamMute(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserVideoStreamEnabled(RongRTCRemoteUser rongRTCRemoteUser, RongRTCAVInputStream rongRTCAVInputStream, boolean b) {

    }

    @Override
    public void onRemoteUserUnpublishResource(RongRTCRemoteUser rongRTCRemoteUser, List<RongRTCAVInputStream> list) {

    }

    @Override
    public void onUserJoined(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onUserLeft(RongRTCRemoteUser rongRTCRemoteUser) {
        for (RongRTCAVInputStream inputStream : rongRTCRemoteUser.getRemoteAVStreams()) {
            if (inputStream.getMediaType() == MediaType.VIDEO) {
                remotes.removeView(inputStream.getRongRTCVideoView());
            }
        }
        Toast.makeText(RoomInfoActivity.this, "房主已退出房间", Toast.LENGTH_SHORT).show();
        removeListener();
        quit();
        Intent intent4 = new Intent();
        intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
        startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
        finish();
    }

    @Override
    public void onUserOffline(RongRTCRemoteUser rongRTCRemoteUser) {

    }

    @Override
    public void onVideoTrackAdd(String s, String s1) {

    }

    @Override
    public void onFirstFrameDraw(String s, String s1) {

    }

    @Override
    public void onLeaveRoom() {

    }

    @Override
    public void onReceiveMessage(Message message) {

    }

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("userId", "");
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.findRoomByNum)
                .addParams("num", mRoomId)
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
                                java.lang.reflect.Type roomType = new TypeToken<Room>(){}.getType();
                                Room room = MyUtils.getGson().fromJson(apiResult.getData().toString(), roomType);
                                if (room.getCreateUserId() != null && room.getCreateUserId().toString().equals(userId)) {
                                    String str = "TA在热忱陪着你，确定要离开房间吗？若离开，本次收益将退回。";
                                    new AlertDialog.Builder(context).setTitle("提醒")
                                            .setMessage(str)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setPositiveButton("确认离开", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    removeListener();
                                                    quitRoom();
                                                    Intent intent4 = new Intent();
                                                    intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                                    startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("暂不离开", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                } else if (room.getJoinUserId() != null && room.getJoinUserId().toString().equals(userId)) {
                                    String str = "离开房间您将失去Ta的陪伴，确定要残忍离开吗？";
                                    new AlertDialog.Builder(context).setTitle("提醒")
                                            .setMessage(str)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setPositiveButton("残忍离开", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    removeListener();
                                                    quitRoom();
                                                    Intent intent4 = new Intent();
                                                    intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                                    startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                                    finish();

                                                }
                                            })
                                            .setNegativeButton("继续陪伴", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                }else {
                                    removeListener();
                                    quit();
                                    Intent intent4 = new Intent();
                                    intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                    startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                    finish();
                                }
                            } else {
                                removeListener();
                                quit();
                                Intent intent4 = new Intent();
                                intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                                startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                                finish();
                            }
                        } else {
                            removeListener();
                            quit();
                            Intent intent4 = new Intent();
                            intent4.setClass(RoomInfoActivity.this, RoomActivity.class);
                            startActivityForResult(intent4, REQ_CODE_FOR_REGISTER);
                            finish();
                        }
                    }
                });

    }

    private void quit() {
        RongRTCEngine.getInstance().quitRoom(mRongRTCRoom.getRoomId(), new RongRTCResultUICallBack() {
            @Override
            public void onUiSuccess() {
                Toast.makeText(RoomInfoActivity.this, "离开房间成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUiFailed(RTCErrorCode rtcErrorCode) {
                //Toast.makeText(RoomInfoActivity.this, "离开房间失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void quitRoom() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        OkHttpUtils
                .post()
                .url(Api.apiHost + Api.quitRoom)
                .addParams("userId", userId)
                .addParams("num", mRoomId)
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
                            quit();
                        }
                    }
                });
    }

    @Override
    public RTCVideoFrame processVideoFrame(RTCVideoFrame rtcVideoFrame) {
        if (beautyFilter == null) {
            beautyFilter = new GPUImageBeautyFilter();
        }
        if (rtcVideoFrame.getCurrentCaptureDataType()) {
            rtcVideoFrame.setOesTextureId(beautyFilter.draw(rtcVideoFrame.getWidth(), rtcVideoFrame.getHeight(), rtcVideoFrame.getOesTextureId()));
        } else {
            //yuv
        }
        return rtcVideoFrame;
    }

    @Override
    public byte[] onLocalBuffer(RTCAudioFrame rtcAudioFrame) {
        return rtcAudioFrame.getBytes();
    }

    @Override
    public byte[] onRemoteBuffer(RTCAudioFrame rtcAudioFrame) {
        return rtcAudioFrame.getBytes();
    }
}
