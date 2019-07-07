package com.hawi.myapplication;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Hawi on 11/12/17.
 */

public class CashImage extends LruCache<String , Bitmap> implements ImageLoader.ImageCache {

    public static int getImageCashSize(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cashSize = maxMemory / 8 ;
        return cashSize;
    }

    public CashImage(){
        this(getImageCashSize());
    }

    public CashImage(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024 ;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }

}
