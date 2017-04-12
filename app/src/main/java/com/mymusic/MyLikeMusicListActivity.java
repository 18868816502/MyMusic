package com.mymusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mymusic.Bean.Mp3Bean;
import com.mymusic.adapter.MyMusicListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyLikeMusicListActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView listView;
    private MyApplication app;
    private ArrayList<Mp3Bean> likeMp3Infos;
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
            List<Mp3Bean> list = app.dbUtils.findAll(Selector.from(Mp3Bean.class).where("isLike","=","1"));
            if(list==null||list.size()==0){
                return;
            }
            likeMp3Infos= (ArrayList<Mp3Bean>) list;
            adapter = new MyMusicListAdapter(this, likeMp3Infos);
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
        if(playService.getChangePlayList()!=PlayService.LIKE_MUSIC_LIST){
            playService.setMp3Infos(likeMp3Infos);
            playService.setChangePlayList(PlayService.LIKE_MUSIC_LIST);
        }
        playService.play(position);
        savePlayRecord();
    }
    /**
     * 保存播放记录
     */
    private void savePlayRecord(){
        //获取当前正在播放的音乐对象
        Mp3Bean mp3Bean = playService.getMp3Infos().get(playService.getCurrentPosition());
        try {
            Mp3Bean playRecordMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Bean.class).where("mp3BeanId","=",mp3Bean.getMp3BeanId()));
            if(playRecordMp3Info==null){
                mp3Bean.setPlayTime(System.currentTimeMillis());
                app.dbUtils.save(mp3Bean);
            }else{
                mp3Bean.setPlayTime(System.currentTimeMillis());
                app.dbUtils.update(playRecordMp3Info,"playTime");
            }
        } catch (DbException e) {
            e.printStackTrace();

        }
    }
}
