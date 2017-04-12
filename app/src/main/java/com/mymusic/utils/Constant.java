package com.mymusic.utils;

/**
 * Created by Administrator on 2017/3/14.
 */

public class Constant {
    public static String SP_NAME = "myMusic";
    public static String DB_NAME = "myMusic.db";
    public static final int PLAY_RECORD_NUM = 10;//最近播放显示的最大条数

    //百度音乐地址
    public static final String BAIDU_URL="http://music.baidu.com/";
    //热歌榜
    public static final String BAIDU_DAYHOT="top/dayhot/?pst=shouyeTop";
    //搜索
    public static final String BAIDU_SEARCH="/search/song";


    //成功标记
    public static final int SUCCESS = 1;
    //失败标记
    public static final int FAILED = 2;

    public static final String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.2372.400 QQBrowser/9.5.10548.400";

    public static final String DIR_MUSIC="/mymusic/music";
    public static final String DIR_LRC="/mymusic/lrc";
}
