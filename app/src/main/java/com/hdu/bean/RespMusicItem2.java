package com.hdu.bean;


public class RespMusicItem2 {
    private String type;
    private String link;
    private long songid;
    private String title;
    private String author;
    private String url;
    private String pic;

    public RespMusicItem2() {
    }

    public RespMusicItem2(String type, String link, long songid, String title, String author, String url, String pic) {
        this.type = type;
        this.link = link;
        this.songid = songid;
        this.title = title;
        this.author = author;
        this.url = url;
        this.pic = pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getSongid() {
        return songid;
    }

    public void setSongid(long songid) {
        this.songid = songid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "RespMusicItem{" +
                "type='" + type + '\'' +
                ", link='" + link + '\'' +
                ", songid=" + songid +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
