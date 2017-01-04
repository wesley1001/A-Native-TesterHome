package com.testerhome.nativeandroid.views;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.SearchResultFragment;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

/**
 * Created by vclub on 15/11/12.
 */
public class SearchActivity extends BackBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("搜索");

        if (getIntent().hasExtra("keyword")) {
            String keyword = getIntent().getStringExtra("keyword");
            setCustomTitle("搜索-" + keyword);
            setupView(keyword);
        } else
            finish();

    }

    private void setupView(String keyword) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                SearchResultFragment.newInstance(keyword))
                .commit();
    }
}
