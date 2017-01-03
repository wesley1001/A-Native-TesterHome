package com.testerhome.nativeandroid.fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;

import butterknife.BindView;

/**
 * Created by vclub on 15/9/15.
 */
public class HomeFragment extends BaseFragment {


    @BindView(R.id.tl_topics)
    TabLayout tabLayoutTopicsTab;

    @BindView(R.id.vp_topics)
    ViewPager viewPagerTopics;

    private TopicViewPagerAdapter mAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void setupView() {
        mAdapter = new TopicViewPagerAdapter(getChildFragmentManager());
        viewPagerTopics.setAdapter(mAdapter);
        viewPagerTopics.setOffscreenPageLimit(4);

        tabLayoutTopicsTab.setupWithViewPager(viewPagerTopics);
    }

    public class TopicViewPagerAdapter extends FragmentPagerAdapter {

        private String[] typeName = {"最新","精华","最热","沙发"};
        private String[] typeValue = {Config.TOPICS_TYPE_RECENT,
                                        Config.TOPICS_TYPE_EXCELLENT,
                                        Config.TOPICS_TYPE_POPULAR,
                                        Config.TOPICS_TYPE_NO_REPLY};

        public TopicViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TopicsListFragment.newInstance(typeValue[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return typeName[position];
        }

        @Override
        public int getCount() {
            return typeName.length;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("homefragment","destroy");
    }
}
