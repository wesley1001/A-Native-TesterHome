package com.testerhome.nativeandroid.views;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.TopicsListFragment;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

/**
 * Created by libin on 16/7/20.
 */
public class TopicListActivity extends BackBaseActivity {

    private String nodeName;
    private int nodeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        if (getIntent().hasExtra("nodeId")) {
            nodeId = getIntent().getIntExtra("nodeId", 0);

            if (getIntent().hasExtra("title")) {
                nodeName = getIntent().getStringExtra("title");
                setCustomTitle(nodeName);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, TopicsListFragment.newInstance(nodeId))
                    .commit();
        } else {
            finish();
        }
    }
}
