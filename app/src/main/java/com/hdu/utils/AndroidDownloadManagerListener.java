package com.hdu.utils;

public interface AndroidDownloadManagerListener {
    void onPrepare();

    void onSuccess(String path);

    void onFailed(Throwable throwable);
}
