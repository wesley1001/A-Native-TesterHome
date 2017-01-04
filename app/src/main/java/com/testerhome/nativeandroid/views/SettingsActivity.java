package com.testerhome.nativeandroid.views;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.views.base.BaseActivity;

public class SettingsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }

    protected void setupToolbar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
    }


    public boolean enableTheme() {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
