package com.hdu.api;

public interface ApiResponseCallback<T> {
    void onSuccess(T response);

    void onFailure(Exception e);
}