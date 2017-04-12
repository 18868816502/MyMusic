package com.mymusic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/8.
 */

public class LrcFragment extends Fragment {
    public static LrcFragment newInstance() {
        LrcFragment net = new LrcFragment();
        Bundle b = new Bundle();
        return net;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_viewpager_lrc, null);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
