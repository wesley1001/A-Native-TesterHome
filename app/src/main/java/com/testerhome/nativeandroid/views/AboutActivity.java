package com.testerhome.nativeandroid.views;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by vclub on 15/10/22.
 */
public class AboutActivity extends BackBaseActivity {

    @BindView(R.id.tv_app_version)
    TextView tvVersion;

    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setCustomTitle("关于");

        tvVersion.setText(getResources().getString(R.string.app_name)
                .concat("\nV")
                .concat(getAppInfo()));

        uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
    }


    public boolean enableTheme() {
        return true;
    }

    private String getAppInfo() {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return String.format("%s build %s", info.versionName, info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        }

        return "";
    }

    private int test;
    private static final String TAG = "AboutActivity";

    @OnClick(R.id.tv_app_version)
    void test() {

        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_NO) {

            test = AppCompatDelegate.MODE_NIGHT_YES;
            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
        } else {
            test = AppCompatDelegate.MODE_NIGHT_NO;
            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
        }

        Log.d(TAG, "test: " + AppCompatDelegate.getDefaultNightMode());
    }
}
