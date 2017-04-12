package com.mymusic.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mymusic.MyApplication;

/**
 * Created by Administrator on 2017/4/7.
 */

public class AppUtils {
    /**
     * 键盘隐藏
     */
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) MyApplication.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
