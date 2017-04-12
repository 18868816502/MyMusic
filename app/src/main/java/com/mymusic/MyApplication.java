package com.mymusic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.lidroid.xutils.DbUtils;
import com.mymusic.utils.Constant;

/**
 * Created by Administrator on 2017/3/14.
 */

public class MyApplication extends Application {
    public static SharedPreferences sp;
    public static Context context;
public static DbUtils dbUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE);
        dbUtils=DbUtils.create(getApplicationContext(),Constant.DB_NAME);
        context=getApplicationContext();
    }
}
