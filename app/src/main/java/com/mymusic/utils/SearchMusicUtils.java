package com.mymusic.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mymusic.Bean.SearchResultBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SearchMusicUtils {
    private static final int SIZE = 20;//查询条数
    private static final String URL = Constant.BAIDU_URL + Constant.BAIDU_SEARCH;
    private static SearchMusicUtils sInstance;
    private OnSearhResultListener mListener;

    private ExecutorService mThreadPool;

    public synchronized static SearchMusicUtils getsInstance() {
        if (sInstance == null) {
            try {
                sInstance = new SearchMusicUtils();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

    private SearchMusicUtils() throws ParserConfigurationException {
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public SearchMusicUtils setListener(OnSearhResultListener l) {
        mListener = l;
        return this;
    }

    public void search(final String key, final int page) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.SUCCESS:
                        if (mListener != null) {
                            mListener.onSearchResult((ArrayList<SearchResultBean>) msg.obj);
                        }
                        break;
                    case Constant.FAILED:
                        if (mListener!=null){
                            mListener.onSearchResult(null);
                        }
                        break;
                }
            }
        };
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<SearchResultBean> results = getMusicList(key, page);
                if (results == null) {
                    handler.sendEmptyMessage(Constant.FAILED);
                    return;
                }
                handler.obtainMessage(Constant.SUCCESS,results).sendToTarget();
            }
        });
    }

    /**
     * 使用Jsoup请求网络解析数据
     *
     * @param key
     * @param page
     * @return
     */
    private ArrayList<SearchResultBean> getMusicList(final String key, final int page) {
        final String start = String.valueOf((page - 1) * SIZE);
        ArrayList<SearchResultBean> searchResults = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).data("key", key, "start", start, "size", String.valueOf(SIZE))
                    .userAgent(Constant.USER_AGENT)
                    .timeout(60 * 1000).get();

            Elements songTitles = doc.select("div.song-item.clearfix");
            Elements songInfos;
            TAG:
            for (Element song : songTitles) {
                songInfos = song.getElementsByTag("a");
                SearchResultBean bean = new SearchResultBean();
                for (Element info : songInfos) {
                    //收费的歌曲
                    if (info.attr("href").startsWith("http://y.baidu.com/song/")) {
                        continue TAG;
                    }
                    //跳转到百度音乐盒的歌曲
                    if (info.attr("href").equals("#") && !TextUtils.isEmpty(info.attr("data-songdata"))) {
                        continue TAG;
                    }
                    //歌曲链接
                    if (info.attr("href").startsWith("/song")) {
                        bean.setMusicName(info.text());
                        bean.setUrl(info.attr("href"));
                    }
                    //歌手链接
                    if (info.attr("href").startsWith("/data")) {
                        bean.setArtist(info.text());
                    }
                    //专辑链接
                    if (info.attr("href").startsWith("/album")) {
                        bean.setArtist(info.text().replaceAll("《|》", ""));
                    }
                }
                searchResults.add(bean);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    public interface OnSearhResultListener {
        public void onSearchResult(ArrayList<SearchResultBean> results);
    }
}
