package com.mymusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.mymusic.Bean.Mp3Bean;
import com.mymusic.utils.MediaUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 音乐播放的服务组件
 * 实现功能
 * 1.播放
 * 2.暂停
 * 3.上一首
 * 4.下一首
 * 5.获取当前的播放进度
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer;
    private int currentPosition;//当前播放的位置
    public ArrayList<Mp3Bean> mp3Infos;
    private MusicUpdateListener musicUpdateListener;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isPause = false;
    public static final int ORDER_PLAY = 1, RANDOM_PLAY = 2, SINGLE_PLAY = 3;//列表循环、随机播放、单曲循环
    private int play_mode = ORDER_PLAY;
    private Random random = new Random();//产生随机播放数

    //切换播放列表
    public static final int MY_MUSIC_LIST = 1;//我的音乐列表
    public static final int LIKE_MUSIC_LIST = 2;//我喜欢的列表
    public static final int PLAY_RECORD_MUSIC_LIST = 3;//最近播放的列表
    private int changePlayList = MY_MUSIC_LIST;


    public PlayService() {
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (play_mode) {
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size() - 1));
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public void setMp3Infos(ArrayList<Mp3Bean> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    public ArrayList<Mp3Bean> getMp3Infos() {
        return this.mp3Infos;
    }

    public int getChangePlayList() {
        return changePlayList;
    }

    public void setChangePlayList(int changePlayList) {
        this.changePlayList = changePlayList;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication myApplication = (MyApplication) getApplication();
        currentPosition = myApplication.sp.getInt("currentPosition", 0);
        play_mode = myApplication.sp.getInt("play_mode", PlayService.ORDER_PLAY);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mp3Infos = MediaUtils.getMp3Infos(this);
        es.execute(updateStatusRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && !es.isShutdown()) {
            es.shutdown();//关闭线程池
            es = null;
        }
    }

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public boolean isPause() {
        return isPause;
    }

    //播放
    public void play(int position) {
        Mp3Bean mp3Bean = null;
        if (position < 0 || position >= mp3Infos.size()) {
            position = 0;
        }
        mp3Bean = mp3Infos.get(position);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(mp3Bean.getUrl()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            currentPosition = position;
            isPause = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (musicUpdateListener != null) {
            musicUpdateListener.onChange(currentPosition);
        }

    }

    //暂停
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    //下一首
    public void next() {
        if (currentPosition + 1 > mp3Infos.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);
    }

    //上一首
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);
    }

    //从头开始播放
    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();

        }
    }

    //判断是否正在播放
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 获取当前进度
     *
     * @return
     */
    public int getCurrentProgress() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    //更新状态的接口
    public interface MusicUpdateListener {
        public void onPublish(int progress);

        public void onChange(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
