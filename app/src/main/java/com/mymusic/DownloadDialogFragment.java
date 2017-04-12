package com.mymusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.sample.MainActivity;
import com.mymusic.Bean.SearchResultBean;
import com.mymusic.adapter.NetMusicAdapter;
import com.mymusic.utils.DownloadUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class DownloadDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private SearchResultBean searchResultBean;//当前需要下载的歌曲
    private String[] items;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        items = new String[]{"下载", "取消"};
    }

    public static DownloadDialogFragment newInstance(SearchResultBean searchResultBean) {
        DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
        downloadDialogFragment.searchResultBean = searchResultBean;
        return downloadDialogFragment;
    }

    // 创建对话框方法
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://执行下载
                        downloadMusic();
                        break;
                    case 1://取消
                        dialog.dismiss();
                        break;
                }
            }


        });
        return builder.show();
    }

    /**
     * 下载网络音乐
     */
    private void downloadMusic() {
        Toast.makeText(mainActivity,"正在下载:"+searchResultBean.getMusicName(),Toast.LENGTH_SHORT).show();
        DownloadUtils.getsInstance().setListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownload(String mp3Url) {
                Toast.makeText(mainActivity,"歌曲下载成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mainActivity,error,Toast.LENGTH_SHORT).show();
            }
        }).download(searchResultBean);
    }




}
