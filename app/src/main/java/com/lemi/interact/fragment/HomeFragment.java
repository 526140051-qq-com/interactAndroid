package com.lemi.interact.fragment;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.lemi.interact.R;

public class HomeFragment extends Fragment {

    private VideoView videoView;

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

        //加载指定的视频文件
        String path = "https://voteserver.oss-cn-beijing.aliyuncs.com/voteserver/file/07d632cfc05d41d685cc72fb16bdad32.mp4";
        videoView.setVideoPath(path);
        //创建MediaController对象
        MediaController mediaController = new MediaController(getActivity());
        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);
        //让VideoView获取焦点
        videoView.requestFocus();
    }

}
