package com.testerhome.nativeandroid.views.base;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vclub on 15/9/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.toolbar_title)
    TextView customTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(enableTheme()){
            loadTheme();
        } else {
            setTheme(R.style.theme_light);
        }
    }

    public boolean enableTheme(){
        return true;
    }

    private void loadTheme(){
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsFragment.KEY_PREF_THEME, false)) {
            setTheme(R.style.theme_dark);
        } else {
            setTheme(R.style.theme_light);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        setupToolbar();
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public void setCustomTitle(String title) {
        if (customTitle != null) {
            customTitle.setText(title);
        }
    }

}
