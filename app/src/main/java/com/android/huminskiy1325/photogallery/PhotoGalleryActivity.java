package com.android.huminskiy1325.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


public class PhotoGalleryActivity extends SingleFragmenActivity {

    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PhotoGalleryFragment fragment = (PhotoGalleryFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
//        super.onNewIntent(intent);

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Received a new search query: " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(FlickrFetch.PREF_SEARCH_QUERY, query)
                    .commit();
        }
        fragment.updateItems(0);
    }

    @Override
    protected Fragment createFragment() {
        return new PhotoGalleryFragment();
    }
}
