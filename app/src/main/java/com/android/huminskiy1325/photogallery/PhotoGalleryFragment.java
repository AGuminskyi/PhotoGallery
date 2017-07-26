package com.android.huminskiy1325.photogallery;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        setHasOptionsMenu(true);
//        new FetcItemTask().execute(current_page);
        updateItems(current_page);
//        FlickrFetch fetch = new FlickrFetch();

//        mThumbnailThread = new ThumbnailDownloader<>();
        mThumbnailThread = new ThumbnailDownloader<>(new Handler());
        mThumbnailThread.setListener(new Listener<ImageView>() {
            @Override
            public void onThumbnaiDownloaded(ImageView view, Bitmap thumbnail) {
                if (isVisible()) {
                    view.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    public void updateItems(int page){
        new FetcItemTask().execute(page);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView)searchItem.getActionView();
        // Получение данных из файла searchable.xml
                                    // в виде объекта SearchableInfo
        SearchManager searchManager = (SearchManager)getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = getActivity().getComponentName();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
        searchView.setSearchableInfo(searchInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetch.PREF_SEARCH_QUERY, null)
                        .commit();
                updateItems(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
//            String query = "android"; // Только для тестирования
            Activity activity = getActivity();
            if(activity == null){
                return  new ArrayList<GalleryItem>();
            }
            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetch.PREF_SEARCH_QUERY, null);
            if (query != null) {
                return new FlickrFetch().search(query);
            } else {
                return new FlickrFetch().fetchItems(params[0]);
            }
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
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
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
