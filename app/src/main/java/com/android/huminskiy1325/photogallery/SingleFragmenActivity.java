package com.android.huminskiy1325.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cubru on 14.04.2017.
 */

public abstract class SingleFragmenActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    protected int getLauoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null)
            fragment = createFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit();
    }
}
