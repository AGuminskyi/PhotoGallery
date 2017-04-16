package com.android.huminskiy1325.photogallery;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cubru on 15.04.2017.
 */

public class FlickrFetch {

    public static final String ENDPOINT = "https://www.flickr.com/services/api/";
    public static final String API_KEY = "0bc3f97c92abde09b64702b72a3005cb";
    public static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    public static final String PARAM_EXTRAS = "extras";
    public static final String EXTRA_SMALL_URL = "url_s";

    public static final String TAG = "FlickrFetch";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = httpURLConnection.getInputStream();//связь с конечной точкой
            // будет установлена только после вызова getInputStream()
            //До этого момента вы не сможете получить действительный код ответа.
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItem(){
        try {
            String url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GET_RECENT) //кодирует строки запросов
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                    .build().toString();
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml " + xmlString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch item",e);
        }
    }
}
