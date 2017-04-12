package com.mymusic.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */

public class MyPagerAdapter extends PagerAdapter{
    private List<View> viewList;
    public MyPagerAdapter(List<View> viewList){
        this.viewList=viewList;
    }
    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position),0);
        return viewList.get(position);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
