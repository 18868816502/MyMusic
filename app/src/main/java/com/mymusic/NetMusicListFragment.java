package com.mymusic;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.MainActivity;
import com.mymusic.Bean.SearchResultBean;
import com.mymusic.adapter.NetMusicAdapter;
import com.mymusic.utils.AppUtils;
import com.mymusic.utils.Constant;
import com.mymusic.utils.SearchMusicUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class NetMusicListFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private MainActivity mainActivity;
    private LinearLayout ll_search_btn_container, ll_search_container, ll_loadLayout;
    private EditText et_search_content;
    private ImageButton ib_search_btn;
    private ProgressBar pb_search_wait;
    private ListView listView_net_music;
    private SearchResultBean searchResultBean;
    private NetMusicAdapter netMusicAdapter;
    private ArrayList<SearchResultBean> searchResults = new ArrayList<>();
    private int page = 1;//搜索音乐的页码

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static NetMusicListFragment newInstance() {
        NetMusicListFragment net = new NetMusicListFragment();
        Bundle b = new Bundle();
        return net;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //UI组件的初始化
        View view = inflater.inflate(R.layout.net_music_list_layout, null);
        ll_search_btn_container = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
        ll_search_container = (LinearLayout) view.findViewById(R.id.ll_search_container);
        ll_loadLayout = (LinearLayout) view.findViewById(R.id.ll_loadLayout);
        et_search_content = (EditText) view.findViewById(R.id.et_search_content);
        ib_search_btn = (ImageButton) view.findViewById(R.id.ib_search_btn);
        pb_search_wait = (ProgressBar) view.findViewById(R.id.pb_search_wait);
        listView_net_music = (ListView) view.findViewById(R.id.listView_net_music);

        listView_net_music.setOnItemClickListener(this);
        ll_search_btn_container.setOnClickListener(this);
        ib_search_btn.setOnClickListener(this);
        loadNetData();
        return view;
    }

    /**
     * 加载网络音乐
     */
    private void loadNetData() {
        ll_loadLayout.setVisibility(View.VISIBLE);
        new LoadNetDataTask().execute(Constant.BAIDU_URL + Constant.BAIDU_DAYHOT);
    }

    //列表项的单击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= netMusicAdapter.getSearchResults().size() || position < 0) {
            return;
        }
        showDownLoadDialog(position);
    }

    /**
     * 下载弹窗
     *
     * @param position
     */
    private void showDownLoadDialog(final int position) {
        DownloadDialogFragment downloadFragment = DownloadDialogFragment.newInstance(searchResults.get(position));
        downloadFragment.show(getFragmentManager(), "download");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_search_btn_container:
                ll_search_btn_container.setVisibility(View.GONE);
                ll_search_container.setVisibility(View.VISIBLE);
                break;
            //搜索歌曲
            case R.id.ib_search_btn:
                searchMusic();
                break;
        }
    }

    /**
     * 加载网络音乐的异步任务
     */
    class LoadNetDataTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ll_loadLayout.setVisibility(View.VISIBLE);
            listView_net_music.setVisibility(View.GONE);
            searchResults.clear();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String url = params[0];
            try {
                Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6 * 100).get();
                Elements songTitles = doc.select("span.song-title");
                Elements artists = doc.select("span.author_list");
                for (int i = 0; i < songTitles.size(); i++) {
                    SearchResultBean bean = new SearchResultBean();
                    Elements urls = songTitles.get(i).getElementsByTag("a");
                    bean.setUrl(urls.get(0).attr("href"));
                    bean.setMusicName(urls.get(0).text());
                    Elements artistElements = artists.get(i).getElementsByTag("a");
                    bean.setArtist(artistElements.get(0).text());

                    bean.setAlbum("热歌榜");
                    searchResults.add(bean);
//                    System.out.println(searchResultBean.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                netMusicAdapter = new NetMusicAdapter(mainActivity, searchResults);
                listView_net_music.setAdapter(netMusicAdapter);
                listView_net_music.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.footview_layout, null));
            }
            ll_loadLayout.setVisibility(View.GONE);
            listView_net_music.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 搜索音乐
     */
    private void searchMusic() {
        AppUtils.hideInputMethod(et_search_content);
        ll_search_btn_container.setVisibility(View.VISIBLE);
        ll_search_container.setVisibility(View.GONE);
        String key = et_search_content.getText().toString();
        if (TextUtils.isEmpty(key)) {
            Toast.makeText(mainActivity, "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }
        ll_loadLayout.setVisibility(View.VISIBLE);

        SearchMusicUtils.getsInstance().setListener(new SearchMusicUtils.OnSearhResultListener() {
            @Override
            public void onSearchResult(ArrayList<SearchResultBean> results) {
                ArrayList<SearchResultBean> sr = netMusicAdapter.getSearchResults();
                sr.clear();
                sr.addAll(results);
                netMusicAdapter.notifyDataSetChanged();
                ll_loadLayout.setVisibility(View.GONE);
            }
        }).search(key, page);
    }
}
