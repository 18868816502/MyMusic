package com.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymusic.Bean.Mp3Bean;
import com.mymusic.Bean.SearchResultBean;
import com.mymusic.R;
import com.mymusic.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class NetMusicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SearchResultBean> mp3BeanList;

    public NetMusicAdapter(Context context, ArrayList<SearchResultBean> mp3Bean) {
        this.context = context;
        this.mp3BeanList = mp3Bean;
    }

    public ArrayList<SearchResultBean> getSearchResults() {
        return mp3BeanList;
    }


    public void setSearchResults(ArrayList<SearchResultBean> mp3BeanList) {
        this.mp3BeanList = mp3BeanList;
    }

    @Override
    public int getCount() {
        return mp3BeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3BeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_net_music_list, null);
            vh = new ViewHolder();
            vh.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            vh.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            vh.tv_singer = (TextView) convertView.findViewById(R.id.tv_singer);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        SearchResultBean bean = mp3BeanList.get(position);
        vh.tv_title.setText(bean.getMusicName());
        vh.tv_singer.setText(bean.getArtist());

        return convertView;
    }


    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_singer;
    }
}
