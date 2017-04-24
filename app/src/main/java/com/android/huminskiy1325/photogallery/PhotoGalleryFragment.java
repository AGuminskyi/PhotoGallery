package com.android.huminskiy1325.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by cubru on 14.04.2017.
 */

public class PhotoGalleryFragment extends Fragment {
    GridView mGridView;
    ArrayList<GalleryItem> mItems;

    int current_page = 1;
    int fetch_page = 0;

    ThumbnailDownloader<ImageView> mThumbnailThread;

    private static final String TAG = "PhotoGalleryFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        new FetcItemTask().execute(current_page);

//        mThumbnailThread = new ThumbnailDownloader<>();
        mThumbnailThread = new ThumbnailDownloader<>(new Handler());
        mThumbnailThread.setListener(new Listener<ImageView>() {
            @Override
            public void onThumbnaiDownloaded(ImageView view, Bitmap thumbnail) {
                if(isVisible()){
                    view.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && fetch_page == current_page) {
                    current_page += 1;
                    new FetcItemTask().execute(current_page);
                }

            }
        });

        setupAdapter();

        return view;
    }

    private class FetcItemTask extends AsyncTask<Integer, Void, ArrayList<GalleryItem>> {
        @Override
        protected ArrayList<GalleryItem> doInBackground(Integer... params) {
//            try {
//                String result = new FlickrFetch().getUrl("http://google.com");
//                Log.i(TAG, "Fetched contents of URL: " + result);
//            }catch (IOException e){
//                Log.i(TAG, "Failed to fetch URL: " + e);
//            }
            return new FlickrFetch().fetchItem(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems)//получает список, загруженный в doInBackground(…)
        {
            if (mItems == null) {
                mItems = galleryItems;
            } else {
                mItems.addAll(galleryItems);
            }
            setupAdapter();
            fetch_page += 1;
        }
    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null)
            return;

        if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }

    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {

        GalleryItemAdapter(List<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.brian_up_close);
            GalleryItem item = getItem(position);
            mThumbnailThread.queueThumbnail(imageView, item.getUrl());
            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }
}
