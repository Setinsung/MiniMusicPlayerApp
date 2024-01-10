package com.hdu.bean;

import java.util.List;

public class FreeMusicAPIResp {
    private int code;
    private String error;
    private List<RespMusicItem2> data;

    public FreeMusicAPIResp() {
    }

    public FreeMusicAPIResp(int code, String error, List<RespMusicItem2> data) {
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<RespMusicItem2> getData() {
        return data;
    }

    public void setData(List<RespMusicItem2> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FreeMusicAPIResp{" +
                "code=" + code +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }
}

