package com.mymusic.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.mymusic.Bean.Mp3Bean;
import com.mymusic.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class MediaUtils {
    //获取专辑封面的Uri
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
//    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albums");

    /**
     * 根据歌曲id查询歌曲信息
     */
    public static Mp3Bean getMp3Bean(Context context, long _id) {
        System.out.println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media._ID + "=" + _id, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Mp3Bean mp3Bean = null;
        if (cursor.moveToNext()) {
            mp3Bean = new Mp3Bean();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long albunId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));//
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {
                mp3Bean.setId(id);
                mp3Bean.setTitle(title);
                mp3Bean.setArtist(artist);
                mp3Bean.setAlbum(album);
                mp3Bean.setAlbumId(albunId);
                mp3Bean.setDuration(duration);
                mp3Bean.setSize(size);
                mp3Bean.setUrl(url);
            }
        }
        cursor.close();
        return mp3Bean;
    }

    /**
     * 用于从数据库中查询歌曲的信息(id),保存到List当中
     */
    public static long[] getMp3Info(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID},
                MediaStore.Audio.Media.DURATION + ">=180000", null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        long[] ids = null;
        if (cursor != null) {
            ids = new long[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                ids[i] = cursor.getLong(0);
            }
        }
        cursor.close();
        return ids;
    }

    /**
     * 用于从数据库中查询歌曲的信息(全部),保存到List当中
     */
    public static ArrayList<Mp3Bean> getMp3Infos(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DURATION + ">=180000", null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        ArrayList<Mp3Bean> mp3Infos = new ArrayList<Mp3Bean>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            Mp3Bean mp3Bean = new Mp3Bean();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑
            long albunId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));//
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {
                mp3Bean.setId(id);
                mp3Bean.setTitle(title);
                mp3Bean.setArtist(artist);
                mp3Bean.setAlbum(album);
                mp3Bean.setAlbumId(albunId);
                mp3Bean.setDuration(duration);
                mp3Bean.setSize(size);
                mp3Bean.setUrl(url);
            }
            mp3Infos.add(mp3Bean);
        }
        cursor.close();
        return mp3Infos;
    }

    /**
     * 格式化时间,把毫秒转化为分：秒格式
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + min;
        }
        if (sec.length() == 4) {
            sec = "0" + sec;
        } else if (sec.length() == 3) {
            sec = "00" + sec;
        } else if (sec.length() == 2) {
            sec = "000" + sec;
        } else if (sec.length() == 1) {
            sec = "0000" + sec;
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    /**
     * 获取专辑图片
     */
//    public static Bitmap getDefaultArtwork(Context context, boolean small) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        if (small) {//返回小图片
//            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.ic_music2), null, options);
//        }
//        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.mipmap.ic_music2), null, options);
//    }

    /**
     * 从文件中获取专辑封面位图
     */
    private static Bitmap getArtworkFromFile(Context context, long songId, long albumId) {
        Bitmap bm = null;
        if (albumId < 0 && songId < 0) {
            throw new IllegalStateException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if (albumId < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songId + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            //只进行大小判断
            options.inJustDecodeBounds = true;
            //调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            //我们的目标是在800pixel的画面上显示
            //所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            //我们得到了缩放的比例,现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            //根据options参数,减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取专辑封面位图对象
     */
//    public static Bitmap getArtwork(Context context, long songId, long albumId, boolean allowDefault, boolean small) {
//        if (albumId < 0) {
//            if (songId < 0) {
//                Bitmap bm = getArtworkFromFile(context, songId, -1);
//                if (bm != null) {
//                    return bm;
//                }
//            }
//            if (allowDefault) {
//                return getDefaultArtwork(context, small);
//            }
//            return null;
//        }
//        ContentResolver res = context.getContentResolver();
//        Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
//        if (uri != null) {
//            InputStream in = null;
//            try {
//                in = res.openInputStream(uri);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                //先制定原始大小
//                options.inSampleSize = 1;
//                //只进行大小判断
//                options.inJustDecodeBounds = true;
//                //调用此方法得到options得到图片大小
//                BitmapFactory.decodeStream(in, null, options);
//                if (small) {
////                    options.inSampleSize = computeSampleSize(options, 40);
//                    options.inSampleSize = 4;
//                } else {
//                    options.inSampleSize = 6;
////                    options.inSampleSize = computeSampleSize(options, 600);
//                }
//                //我们得到了缩放的比例,现在开始正式读入Bitmap数据
//                options.inJustDecodeBounds = false;
//                options.inDither = false;
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                in = res.openInputStream(uri);
//                return BitmapFactory.decodeStream(in, null, options);
//            } catch (FileNotFoundException e) {
//                Bitmap bm = getArtworkFromFile(context, songId, albumId);
//                if (bm != null) {
//                    if (bm.getConfig() == null) {
//                        bm = bm.copy(Bitmap.Config.RGB_565, false);
//                        if (bm == null && allowDefault) {
//                            return getDefaultArtwork(context, small);
//                        }
//                    }
//                } else if (allowDefault) {
//                    bm = getDefaultArtwork(context, small);
//                }
//                return bm;
//            } finally {
//                try {
//                    if (in != null) {
//                        in.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }
    private static int computeSampleSize(BitmapFactory.Options options, int i) {
        return 20;
    }


    public static String getAlbumArt(Context context, long album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = context.getContentResolver().query(
                Uri.parse(mUriAlbums + "/" + Long.toString(album_id)),
                projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }

    public static Bitmap getImage(Context context, long album_id) {
        Bitmap bm = null;
//        Cursor currentCursor = context.getCursor("/mnt/sdcard/" + mp3Info);
//        int album_id = currentCursor.getInt(currentCursor
//                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String albumArt = getAlbumArt(context, album_id);
        if (albumArt == null) {
//            mImageView.setBackgroundResource(R.drawable.staring);
        } else {
            bm = BitmapFactory.decodeFile(albumArt);
        }
        return bm;
    }


}
