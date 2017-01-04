package com.testerhome.nativeandroid.views;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.testerhome.nativeandroid.views.base.BaseActivity;

/**
 * Created by libin on 16/6/11.
 */
public class DummyActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(this::finish, 500);
    }
}
