package com.mymusic;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mymusic.Bean.Mp3Bean;
import com.mymusic.adapter.MyMusicListAdapter;
import com.mymusic.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class RecentMusicListActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private MyApplication app;
    private ArrayList<Mp3Bean> recordMp3Infos;
    private MyMusicListAdapter adapter;
    private boolean isChange = false;//用来表示当前播放列表是否为收藏列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like_music_list);
        app = (MyApplication) getApplication();
        init();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    /**
     * 初始化控件
     */
    public void init() {
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        try {
            List<Mp3Bean> list = app.dbUtils.findAll(Selector.from(Mp3Bean.class).where("playTime","!=","0").orderBy("playTime",true).limit(Constant.PLAY_RECORD_NUM));
            if(list==null||list.size()==0){
                return;
            }
            recordMp3Infos = (ArrayList<Mp3Bean>) list;
            adapter = new MyMusicListAdapter(this, recordMp3Infos);
            listView.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(playService.getChangePlayList()!=PlayService.PLAY_RECORD_MUSIC_LIST){
            playService.setMp3Infos(recordMp3Infos);
            playService.setChangePlayList(PlayService.PLAY_RECORD_MUSIC_LIST);
        }
        playService.play(position);
    }
}
