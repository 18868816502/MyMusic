package com.mymusic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.MainActivity;
import com.mymusic.Bean.Mp3Bean;
import com.mymusic.adapter.MyMusicListAdapter;
import com.mymusic.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class PicFragment extends Fragment {
    public TextView tv_title;
    public ImageView iv_bigIcon;
    private MusicPlayActivity musicPlayActivity;
    private Mp3Bean mp3Bean;
    public static PicFragment newInstance() {
        PicFragment net = new PicFragment();
        Bundle b = new Bundle();
        return net;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        musicPlayActivity = (MusicPlayActivity) context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_viewpager_pic, null);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        iv_bigIcon= (ImageView) view.findViewById(R.id.iv_bigIcon);
        return view;
    }
    public void loadData(Mp3Bean mp3Bean ) {
//        mp3Bean=MediaUtils.getMp3Bean(musicPlayActivity,id);
//        tv_title.setText(mp3Bean.getTitle());
    }

}
