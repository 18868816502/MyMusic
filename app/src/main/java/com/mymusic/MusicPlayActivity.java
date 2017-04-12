package com.mymusic;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.douzi.android.view.DefaultLrcBuilder;
import com.douzi.android.view.ILrcBuilder;
import com.douzi.android.view.ILrcView;
import com.douzi.android.view.LrcRow;
import com.douzi.android.view.LrcView;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mymusic.Bean.Mp3Bean;
import com.mymusic.Bean.SearchResultBean;
import com.mymusic.adapter.MyPagerAdapter;
import com.mymusic.utils.Constant;
import com.mymusic.utils.DownloadUtils;
import com.mymusic.utils.MediaUtils;
import com.mymusic.utils.SearchMusicUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ViewPager.OnPageChangeListener {
    private static final int UPDATE_TIME = 0x10;//更新播放时间的标记
    private static final int UPDATE_LRC = 0x20;//更新歌词
    private TextView tv_currentTime, tv_totalTime, tv_title;
    private ImageView iv_playMode, iv_pre, iv_play, iv_next, iv_select, iv_bigIcon;
    private ViewPager viewPager;
    private SeekBar seekBar;
    private static MyHandler myHandler;
    private MyApplication app;

    private LrcView lrcView;
    private ArrayList<View> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        app = (MyApplication) getApplication();
        init();
        initViewPager();

        myHandler = new MyHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        //解除播放服务
        unbindPlayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化控件
     */
    private void init() {
//        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_currentTime = (TextView) findViewById(R.id.tv_currentTime);
        tv_totalTime = (TextView) findViewById(R.id.tv_totalTime);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        iv_playMode = (ImageView) findViewById(R.id.iv_playMode);
        iv_pre = (ImageView) findViewById(R.id.iv_pre);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_select = (ImageView) findViewById(R.id.iv_select);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        iv_playMode.setOnClickListener(this);
        iv_pre.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_select.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
//        fra = getSupportFragmentManager().beginTransaction();
//
//        fragmentList = new ArrayList<Fragment>();
//        picFragment = PicFragment.newInstance();
//        lrcFragment = LrcFragment.newInstance();
//        TextView tv = (TextView)findViewById(R.id.tv_title);
//        System.out.println("tv:"+tv);
//        fra.add(picFragment,PicFragment.class.getName());
//        fra.add(lrcFragment,LrcFragment.class.getName());
//        fra.commit();

//        fragmentList.add(picFragment);
//        fragmentList.add(lrcFragment);
//        viewPager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
//        viewPager.setCurrentItem(0, true);
//        PicFragment picFragment= (PicFragment) ((MyFrageStatePagerAdapter)viewPager.getAdapter()).currentFragment;
//    TextView tv= (TextView) picFragment.getView();
//        System.out.println("tv:"+tv);
    }

    private void initViewPager() {
        View viewPagerPic = getLayoutInflater().inflate(R.layout.item_viewpager_pic, null);
        tv_title = (TextView) viewPagerPic.findViewById(R.id.tv_title);

        iv_bigIcon = (ImageView) viewPagerPic.findViewById(R.id.iv_bigIcon);
        views.add(viewPagerPic);
        View viewPagerLrc = getLayoutInflater().inflate(R.layout.item_viewpager_lrc, null);
        lrcView = (LrcView) viewPagerLrc.findViewById(R.id.lrcView);
        //设置滚动事件
        lrcView.setListener(new ILrcView.LrcViewListener() {
            @Override
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (playService.isPlaying()) {
                    playService.seekTo((int) row.time);
                }
            }
        });
        lrcView.setLoadingTipText("正在加载歌词");
        lrcView.setBackgroundResource(R.mipmap.jb_bg);
        lrcView.getBackground().setAlpha(150);//设置透明度
        views.add(viewPagerLrc);
        viewPager.setAdapter(new MyPagerAdapter(views));
        viewPager.addOnPageChangeListener(this);
//        viewPager.setCurrentItem(0, true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && playService.isPlaying()) {//是否来自用户手动拖拽
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    static class MyHandler extends Handler {
        private MusicPlayActivity musicPlayActivity;
        private WeakReference<MusicPlayActivity> weak;

        public MyHandler(MusicPlayActivity musicPlayActivity) {
            weak = new WeakReference<MusicPlayActivity>(musicPlayActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            musicPlayActivity = weak.get();
            if (musicPlayActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        musicPlayActivity.tv_currentTime.setText(MediaUtils.formatTime((int) msg.obj));
                        break;
                    case UPDATE_LRC:
                        musicPlayActivity.lrcView.seekLrcToTime((int) msg.obj);
                        break;
                    case DownloadUtils.SUCCESS_LRC:
                        musicPlayActivity.loadLRC(new File((String) msg.obj));
                        break;
                    case DownloadUtils.FAILED_LRC:
                        Toast.makeText(musicPlayActivity, "歌词下载失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 加载歌词
     */
    private void loadLRC(File lrcFile) {
        StringBuffer buf = new StringBuffer(1024 * 10);
        char[] chars = new char[1024];
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile)));
            int len = -1;
            while ((len = in.read(chars)) != -1) {
                buf.append(chars, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(buf.toString());
        lrcView.setLrc(rows);
    }

    @Override
    public void publish(int progress) {
//        Message msg = myHandler.obtainMessage(UPDATE_TIME);
//        msg.arg1 = progress;
//        myHandler.sendMessage(msg);
//        seekBar.setProgress(progress);

        myHandler.obtainMessage(UPDATE_TIME, progress).sendToTarget();
        seekBar.setProgress(progress);
        myHandler.obtainMessage(UPDATE_LRC, progress).sendToTarget();
    }

    @Override
    public void change(int position) {
        Mp3Bean mp3Bean = playService.mp3Infos.get(position);
        tv_title.setText(mp3Bean.getTitle());
//        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Bean.getId(), mp3Bean.getAlbumId(), true, false);

        tv_totalTime.setText(MediaUtils.formatTime(mp3Bean.getDuration()));
        if (playService.isPlaying()) {
            iv_play.setImageResource(R.mipmap.ic_pause);
        } else {
            iv_play.setImageResource(R.mipmap.ic_play);
        }
        seekBar.setProgress(0);
        seekBar.setMax((int) mp3Bean.getDuration());
        switch (playService.getPlay_mode()) {
            case PlayService.ORDER_PLAY:
                iv_playMode.setImageResource(R.mipmap.play_order);
                iv_playMode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                iv_playMode.setImageResource(R.mipmap.play_random);
                iv_playMode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                iv_playMode.setImageResource(R.mipmap.play_single);
                iv_playMode.setTag(PlayService.SINGLE_PLAY);
                break;
            default:
                break;
        }
        //初始化收藏状态
        try {
            Mp3Bean likeMp3Bean = app.dbUtils.findFirst(Selector.from(Mp3Bean.class).where("mp3BeanId", "=", getId(mp3Bean)));
            if (likeMp3Bean != null && likeMp3Bean.getIsLike() == 1) {
                iv_select.setImageResource(R.mipmap.ic_select_yes);
            } else {
                iv_select.setImageResource(R.mipmap.ic_select_no);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        //歌词
        String songName = mp3Bean.getTitle();
        String lrcPath = Environment.getExternalStorageDirectory() + Constant.DIR_LRC + "/" + songName + ".lrc";
        File lrcFile = new File(lrcPath);
        if (!lrcFile.exists()) {
            //下载
            SearchMusicUtils.getsInstance().setListener(new SearchMusicUtils.OnSearhResultListener() {
                @Override
                public void onSearchResult(ArrayList<SearchResultBean> results) {
                    SearchResultBean searchResult = results.get(0);
                    String url = Constant.BAIDU_URL + searchResult.getUrl();
                    DownloadUtils.getsInstance().downloadLRC(url, searchResult.getMusicName(), myHandler);
                }
            }).search(songName + " " + mp3Bean.getArtist(), 1);
        } else {
            loadLRC(lrcFile);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //上一首
            case R.id.iv_pre:
                playService.prev();
                break;
            //播放暂停
            case R.id.iv_play:
                if (playService.isPlaying()) {
                    iv_play.setImageResource(R.mipmap.ic_play);
                    playService.pause();
                } else {
                    if (playService.isPause()) {
                        iv_play.setImageResource(R.mipmap.ic_pause);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            //下一首
            case R.id.iv_next:
                playService.next();
                break;
            //播放模式
            case R.id.iv_playMode: {
                switch ((int) iv_playMode.getTag()) {
                    case PlayService.ORDER_PLAY:
                        iv_playMode.setImageResource(R.mipmap.play_random);
                        iv_playMode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.RANDOM_PLAY:
                        iv_playMode.setImageResource(R.mipmap.play_single);
                        iv_playMode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.SINGLE_PLAY:
                        iv_playMode.setImageResource(R.mipmap.play_order);
                        iv_playMode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(MusicPlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }
            //我喜欢的
            case R.id.iv_select:
                Mp3Bean mp3Bean = playService.mp3Infos.get(playService.getCurrentPosition());
                try {
                    Mp3Bean likeMp3Bean = app.dbUtils.findFirst(Selector.from(Mp3Bean.class).where("mp3BeanId", "=", getId(mp3Bean)));

                    if (likeMp3Bean == null) {
                        mp3Bean.setMp3BeanId(mp3Bean.getId());
                        mp3Bean.setIsLike(1);
                        app.dbUtils.save(mp3Bean);
                        System.out.println("save");
                        iv_select.setImageResource(R.mipmap.ic_select_yes);
                    } else {
                        int isLike = likeMp3Bean.getIsLike();
                        if (isLike == 1) {
                            likeMp3Bean.setIsLike(0);
                            iv_select.setImageResource(R.mipmap.ic_select_no);
                        } else {
                            likeMp3Bean.setIsLike(1);
                            iv_select.setImageResource(R.mipmap.ic_select_yes);
                        }
                        app.dbUtils.update(likeMp3Bean, "isLike");
                        System.out.println("update");
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private long getId(Mp3Bean mp3Bean) {
        //初始化收藏状态
        long id = 0;
        switch (playService.getChangePlayList()) {
            case PlayService.MY_MUSIC_LIST:
                id = mp3Bean.getId();
                break;
            case PlayService.LIKE_MUSIC_LIST:
                id = mp3Bean.getMp3BeanId();
                break;
        }
        return id;
    }


}
