package com.lemi.interact.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lemi.interact.R;

public class MyFragment extends Fragment {
        private static final String TAG = "HomeFragment";

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

    }
