package com.mymusic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.sample.MainActivity;
import com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment;
import com.lidroid.xutils.db.sqlite.Selector;
import com.mymusic.Bean.Mp3Bean;
import com.mymusic.adapter.MyMusicListAdapter;
import com.mymusic.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class MyMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    private MainActivity mainActivity;
    private MyMusicListAdapter myMusicListAdapter;
    private ImageView iv_icon, iv_play, iv_next;
    private TextView tv_musicName, tv_singer;
    private ArrayList<Mp3Bean> mp3Infos;
    private boolean isPause = false;//是否为暂停状态
    private int position = 0;//当前所播放的位置

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        Bundle b = new Bundle();
        return my;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout, null);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        iv_icon.setOnClickListener(this);
        iv_play = (ImageView) view.findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);
        iv_next = (ImageView) view.findViewById(R.id.iv_next);
        iv_next.setOnClickListener(this);
        tv_musicName = (TextView) view.findViewById(R.id.tv_musicName);
        tv_singer = (TextView) view.findViewById(R.id.tv_singer);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //绑定播放服务
        mainActivity.bindPlayService();

    }

    @Override
    public void onPause() {
        super.onPause();
        //解除播放服务
        mainActivity.unbindPlayService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 加载本地音乐列表
     */
    public void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        myMusicListAdapter = new MyMusicListAdapter(mainActivity, mp3Infos);
        listView.setAdapter(myMusicListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mainActivity.playService.getChangePlayList() != PlayService.MY_MUSIC_LIST) {
            mainActivity.playService.setMp3Infos(mp3Infos);
            mainActivity.playService.setChangePlayList(PlayService.MY_MUSIC_LIST);
        }
        mainActivity.playService.play(position);
        savePlayRecord();
    }

    /**
     * 保存播放时间
     */
    private void savePlayRecord() {
       Mp3Bean mp3Bean= mainActivity.playService.getMp3Infos().get(mainActivity.playService.getCurrentPosition());
        try {
            Mp3Bean playRecordMp3Info = mainActivity.app.dbUtils.findFirst(Selector.from(Mp3Bean.class).where("mp3BeanId","=",mp3Bean.getId()));
            if(playRecordMp3Info==null){
                mp3Bean.setMp3BeanId(mp3Bean.getId());
                mp3Bean.setPlayTime(System.currentTimeMillis());
                mainActivity.app.dbUtils.save(mp3Bean);
            }else{
                playRecordMp3Info.setPlayTime(System.currentTimeMillis());
                mainActivity.app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * @param position
     */
    public void changeUIStatusOnPlay(int position) {
        if (position >= 0 && position < mainActivity.playService.mp3Infos.size()) {
            Mp3Bean mp3Bean = mainActivity.playService.mp3Infos.get(position);
            tv_musicName.setText(mp3Bean.getTitle());
            tv_singer.setText(mp3Bean.getArtist());
            if (mainActivity.playService.isPlaying()) {
                iv_play.setImageResource(R.mipmap.ic_pause);
            } else {
                iv_play.setImageResource(R.mipmap.ic_play);
            }
//            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Bean.getId(), mp3Bean.getAlbumId(), true, true);
//            iv_icon.setImageBitmap(albumBitmap);
            Bitmap bitmap = MediaUtils.getImage(mainActivity, mp3Bean.getAlbumId());
            if (bitmap != null) {
                iv_icon.setImageBitmap(bitmap);
            }else{
                iv_icon.setImageResource(R.mipmap.ic_music);
            }
            this.position = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                if (mainActivity.playService.isPlaying()) {
                    iv_play.setImageResource(R.mipmap.ic_play);
                    mainActivity.playService.pause();
                } else {
                    if (mainActivity.playService.isPause()) {
                        iv_play.setImageResource(R.mipmap.ic_pause);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.iv_next:
                mainActivity.playService.next();
                break;
            case R.id.iv_icon:
                Intent intent = new Intent(mainActivity, MusicPlayActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
