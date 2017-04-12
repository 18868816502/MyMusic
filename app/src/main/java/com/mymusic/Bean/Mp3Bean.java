package com.mymusic.Bean;

/**
 * 音乐对象
 * Created by Administrator on 2017/3/8.
 */

public class Mp3Bean {
    private long id;
    private long mp3BeanId;//在收藏音乐时用于保存原始ID
    private String title;//歌名
    private String artist;//艺术家
    private String album;//专辑
    private long albumId;
    private long duration;//时长
    private long size;//大小
    private String url;//路径
    private int isMusic;//是否是音乐
    private long playTime;//最近播放时间
    private int isLike;//1  喜欢   0 默认
    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMp3BeanId() {
        return mp3BeanId;
    }

    public void setMp3BeanId(long mp3BeanId) {
        this.mp3BeanId = mp3BeanId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    @Override
    public String toString() {
        return "Mp3Bean{" +
                "id=" + id +
                ", mp3BeanId=" + mp3BeanId +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                '}';
    }
}
