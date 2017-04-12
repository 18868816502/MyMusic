package com.mymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymusic.Bean.Mp3Bean;
import com.mymusic.R;
import com.mymusic.utils.MediaUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class MyMusicListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Mp3Bean> mp3BeanList;

    public MyMusicListAdapter(Context context, ArrayList<Mp3Bean> mp3Bean) {
        this.context = context;
        this.mp3BeanList = mp3Bean;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music_list, null);
            vh = new ViewHolder();
            vh.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            vh.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            vh.tv_singer = (TextView) convertView.findViewById(R.id.tv_singer);
            vh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Mp3Bean mp3Bean = mp3BeanList.get(position);
        vh.tv_title.setText(mp3Bean.getTitle());
        vh.tv_singer.setText(mp3Bean.getArtist());
        vh.tv_time.setText(MediaUtils.formatTime(mp3Bean.getDuration()));

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_singer;
        TextView tv_time;
    }
}
