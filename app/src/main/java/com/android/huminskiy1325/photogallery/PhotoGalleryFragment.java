package com.android.huminskiy1325.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.IOException;

/**
 * Created by cubru on 14.04.2017.
 */

public class PhotoGalleryFragment extends Fragment {
    GridView mGridView;

    private static final String TAG = "PhotoGalleryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetcItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView)view.findViewById(R.id.gridView);

        return view;
    }

    private class FetcItemTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
//            try {
//                String result = new FlickrFetch().getUrl("http://google.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            }catch (IOException e){
//                Log.i(TAG, "Failed to fetch URL: " + e);
//            }
            new FlickrFetch().fetchItem();
            return null;
        }
    }
}
