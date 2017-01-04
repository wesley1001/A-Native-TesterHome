package com.testerhome.nativeandroid.views;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.AccountFavoriteFragment;
import com.testerhome.nativeandroid.fragments.AccountNotificationFragment;
import com.testerhome.nativeandroid.fragments.AccountTopicsFragment;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cvtpc on 2016/1/21.
 */
public class UserInfoActivity extends BackBaseActivity {


    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.id_user_avatar)
    SimpleDraweeView userAvatar;
    @BindView(R.id.login_name)
    TextView userName;
    @BindView(R.id.id_tag_line)
    TextView tagLine;

    private TesterUser mTesterHomeAccount;
    private String loginName;
    private boolean isLoginer ;

    private final String LOGINERVALUE[] = {"我收藏的帖子","我发表的帖子","我的通知"};
    private final String NOMALVALUE[] = {"他收藏的帖子","他发表的帖子"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_info);

        mTesterHomeAccount = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();
        loginName = getIntent().getStringExtra("loginName");

        isLoginer = loginName.equals(mTesterHomeAccount.getLogin());

        setupView();
        initData();

    }


    private void initData() {

        if (!isLoginer) {
            loadUserInfo();
        }else {
            initUserInfo();
        }

    }

    private void loadUserInfo() {
        RestAdapterUtils.getRestAPI(this).getUserInfo(loginName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userResponse -> {
                    if (userResponse != null && userResponse.getUser() != null) {
                        mTesterHomeAccount = userResponse.getUser();
                        initUserInfo();
                    }
                });

    }



    public void initUserInfo() {

        userAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTesterHomeAccount.getAvatar_url())));
        userName.setText(mTesterHomeAccount.getLogin());
        if (mTesterHomeAccount.getTagline()== null) {
            tagLine.setText("这个人很懒,什么都没有");
        } else {
            tagLine.setText(mTesterHomeAccount.getTagline());
        }
    }





    private void setupView() {

        UserInfoViewPagerAdapter userInfoViewPagerAdapter;
        if (isLoginer) {
            userInfoViewPagerAdapter = new UserInfoViewPagerAdapter(getSupportFragmentManager(),LOGINERVALUE);
        } else {
            userInfoViewPagerAdapter = new UserInfoViewPagerAdapter(getSupportFragmentManager(),NOMALVALUE);
        }
        viewPager.setAdapter(userInfoViewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }



    class UserInfoViewPagerAdapter extends FragmentPagerAdapter {


        private  String userValue[];

        public UserInfoViewPagerAdapter(FragmentManager fm, String[] userValue) {
            super(fm);
            this.userValue = userValue;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return AccountFavoriteFragment.newInstance(loginName);
            } else if (position ==1 ) {
                return AccountTopicsFragment.newInstance(loginName);
            } else {
                return AccountNotificationFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return userValue.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return userValue[position];
        }
    }


}
