package com.android.huminskiy1325.photogallery;

import android.graphics.Bitmap;

/**
 * Created by cubru on 24.04.2017.
 */
public interface Listener<Token> {
    void onThumbnaiDownloaded(Token token, Bitmap thumbnail);
}

