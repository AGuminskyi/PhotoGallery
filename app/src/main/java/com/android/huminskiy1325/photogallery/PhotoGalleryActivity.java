package com.android.huminskiy1325.photogallery;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class PhotoGalleryActivity extends SingleFragmenActivity {

    private static final String TAG = "PhotoGalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);
//        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
//        SearchView searchView = (SearchView)searchItem.getActionView();
//        // Получение данных из файла searchable.xml
//        // в виде объекта SearchableInfo
//        SearchManager searchManager = (SearchManager)this.getSystemService(Context.SEARCH_SERVICE);
//        ComponentName name = this.getComponentName();
//        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
//        searchView.setSearchableInfo(searchInfo);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_item_search:
//                this.onSearchRequested();
//                return true;
//            case R.id.menu_item_clear:
//                PreferenceManager.getDefaultSharedPreferences(this)
//                        .edit()
//                        .putString(FlickrFetch.PREF_SEARCH_QUERY, null)
//                        .apply();
//                PhotoGalleryFragment fragment = new PhotoGalleryFragment();
//                fragment.updateItems(0);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

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
