package com.testerhome.nativeandroid.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.fragments.MarkdownFragment;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.fragments.TopicReplyFragment;
import com.testerhome.nativeandroid.models.CollectTopicResonse;
import com.testerhome.nativeandroid.models.CreateReplyResponse;
import com.testerhome.nativeandroid.models.PraiseEntity;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.TopicDetailEntity;
import com.testerhome.nativeandroid.models.TopicDetailResponse;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;
import com.testerhome.nativeandroid.utils.DeviceUtil;
import com.testerhome.nativeandroid.utils.FavoriteUtil;
import com.testerhome.nativeandroid.utils.PraiseUtil;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vclub on 15/9/17.
 */
public class TopicDetailActivity extends BackBaseActivity implements TopicReplyFragment.ReplyUpdateListener {

    private String mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeBackEnable(true)
                .setSwipeEdge(200)
                .setSwipeEdgePercent(0.02f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
                .setClosePercent(0.2f)//触发关闭Activity百分比
                .setSwipeRelateEnable(false)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(500);//activity联动时的偏移量。默认500px。

        setCustomTitle("帖子详情");
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        Uri uri = getIntent().getData();
        if (uri != null){
            mTopicId = uri.getLastPathSegment();
            setupView();
            loadInfo();
        }else if (getIntent().hasExtra("topic_id")) {
            mTopicId = getIntent().getStringExtra("topic_id");

            setupView();
            loadInfo();
        } else if (getIntent().hasExtra("topic")) {

            TopicEntity topic = getIntent().getParcelableExtra("topic");
            mTopicId = topic.getId();

            setTopicInfo(topic);

            setupView();
            loadInfo();
        } else {
            finish();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCurrentUser == null) {
            mCurrentUser = TesterHomeAccountService.getInstance(this).getActiveAccountInfo();

            if (!TextUtils.isEmpty(mCurrentUser.getRefresh_token())
                    && mCurrentUser.getExpireDate() <= System.currentTimeMillis()) {
                AuthenticationService.refreshToken(getApplicationContext(),
                        mCurrentUser.getRefresh_token());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
    }

    @Override
    public boolean enableTheme() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            final Intent target = new Intent(Intent.ACTION_SEND);
            target.setType("text/plain");
            target.putExtra(Intent.EXTRA_TITLE, tvDetailTitle.getText().toString());
            target.putExtra(Intent.EXTRA_TEXT, String.format("https://testerhome.com/topics/%s", mTopicId));
            startActivity(Intent.createChooser(target, "分享到"));
        }
        return super.onOptionsItemSelected(item);
    }

    @BindView(R.id.tab_layout)
    TabLayout tabLayoutTopicsTab;

    @BindView(R.id.view_pager)
    ViewPager viewPagerTopics;

    private TopicDetailPagerAdapter mAdapter;

    private void setupView() {
        mAdapter = new TopicDetailPagerAdapter(getSupportFragmentManager());
        viewPagerTopics.setAdapter(mAdapter);

        tabLayoutTopicsTab.setupWithViewPager(viewPagerTopics);
    }

    private void setTopicInfo(TopicEntity topicInfo) {

        tvDetailTitle.setText(topicInfo.getTitle());
        tvDetailName.setText(topicInfo.getNode_name().concat(" • "));
        tvDetailUsername.setText(TextUtils.isEmpty(topicInfo.getUser().getLogin()) ?
                "匿名用户" : topicInfo.getUser().getName());
        tvDetailPublishDate.setText(StringUtils.formatPublishDateTime(
                topicInfo.getCreated_at()).concat(" • ")
                .concat("-").concat("次阅读"));
        sdvDetailUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(topicInfo.getUser().getAvatar_url())));
        sdvDetailUserAvatar.setOnClickListener(view -> {
            if (mTopicEntity != null && mTopicEntity.getUser() != null)
                startActivity(new Intent().setClass(TopicDetailActivity.this, UserInfoActivity.class).
                        putExtra("loginName", mTopicEntity.getUser()
                                .getLogin()));
        });
    }

    private MarkdownFragment mMarkdownFragment;
    private TopicReplyFragment mTopicReplyFragment;

    public void updateReplyCount(int count) {
        if (tvDetailRepliesCount != null) {
            if (count > mTopicEntity.getReplies_count())
                tvDetailRepliesCount.setText(getString(R.string.reply_count_info, count));
        }
    }

    public class TopicDetailPagerAdapter extends FragmentPagerAdapter {

        private String[] typeName = {"帖子", "评论"};

        public TopicDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (mMarkdownFragment == null) {
                    mMarkdownFragment = new MarkdownFragment();
                }
                return mMarkdownFragment;
            } else {
                if (mTopicReplyFragment == null) {
                    mTopicReplyFragment = TopicReplyFragment.newInstance(mTopicId, TopicDetailActivity.this);
                }
                return mTopicReplyFragment;
            }
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

    @BindView(R.id.tv_detail_title)
    TextView tvDetailTitle;
    @BindView(R.id.sdv_detail_user_avatar)
    SimpleDraweeView sdvDetailUserAvatar;
    @BindView(R.id.tv_detail_name)
    TextView tvDetailName;
    @BindView(R.id.tv_detail_username)
    TextView tvDetailUsername;
    @BindView(R.id.tv_detail_publish_date)
    TextView tvDetailPublishDate;
    @BindView(R.id.tv_detail_replies_count)
    TextView tvDetailRepliesCount;

    TopicDetailEntity mTopicEntity;

    private void loadInfo() {

        RestAdapterUtils.getRestAPI(this).getTopicById(mTopicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TopicDetailResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mFabAddComment != null) {
                            Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                            Log.e("TopicDetailActivity", "Error:" + t.getMessage());
                        }
                    }

                    @Override
                    public void onNext(TopicDetailResponse topicDetailResponse) {
                        if (topicDetailResponse != null) {
                            mTopicEntity = topicDetailResponse.getTopic();
                            if (tvDetailTitle == null) {
                                return;
                            }
                            tvDetailTitle.setText(mTopicEntity.getTitle());
                            tvDetailName.setText(mTopicEntity.getNode_name().concat(" • "));
                            tvDetailUsername.setText(TextUtils.isEmpty(mTopicEntity.getUser().getLogin()) ?
                                    "匿名用户" : mTopicEntity.getUser().getName());
                            tvDetailPublishDate.setText(StringUtils.formatPublishDateTime(
                                    mTopicEntity.getCreated_at()).concat(" • ")
                                    .concat(mTopicEntity.getHits()).concat("次阅读"));
                            sdvDetailUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(mTopicEntity.getUser().getAvatar_url())));
                            sdvDetailUserAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent().setClass(TopicDetailActivity.this, UserInfoActivity.class).
                                            putExtra("loginName", mTopicEntity.getUser()
                                                    .getLogin()));
                                }
                            });

                            tvDetailPraise.setText(String.format("赞(%s)", topicDetailResponse.getTopic().getLikes_count()));
                            // 用户回复数
                            tvDetailRepliesCount.setText(getString(R.string.reply_count_info, mTopicEntity.getReplies_count()));

                            if (PraiseUtil.hasPraised(TopicDetailActivity.this, mTopicId)) {
                                tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart_off, 0, 0, 0);
                            } else {
                                tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_heart, 0, 0, 0);
                            }

                            if (FavoriteUtil.hasFavorite(TopicDetailActivity.this, mTopicId)) {
                                tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark_off, 0, 0, 0);
                            } else {
                                tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_bookmark, 0, 0, 0);
                            }

                            if (mMarkdownFragment != null) {
                                mMarkdownFragment.showWebContent(topicDetailResponse.getTopic().getBody_html());
                            }
                        }
                    }
                });


    }

    private TesterUser mCurrentUser;
    @BindView(R.id.tv_detail_collect)
    TextView tvDetailCollect;
    @BindView(R.id.tv_detail_praise)
    TextView tvDetailPraise;

    boolean isFavorite = false;

    @OnClick(R.id.tv_detail_collect)
    void onDetailCollectClick() {
        if (mCurrentUser == null || TextUtils.isEmpty(mCurrentUser.getLogin())) {
            Snackbar.make(mFabAddComment, "请先登录客户端", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (FavoriteUtil.hasFavorite(this, mTopicId)) {
            RestAdapterUtils.getRestAPI(this).uncollectTopic(mTopicId, mCurrentUser.getAccess_token())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CollectTopicResonse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(CollectTopicResonse collectTopicResonse) {
                            if (collectTopicResonse != null) {
                                Snackbar.make(mFabAddComment, "取消收藏成功", Snackbar.LENGTH_SHORT).show();
                                FavoriteUtil.delFavorite(TopicDetailActivity.this, mTopicId);
                                if (tvDetailCollect != null) {
                                    tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_off, 0, 0, 0);
                                }
                            }
                        }
                    });
        } else {
            RestAdapterUtils.getRestAPI(this).collectTopic(mTopicId, mCurrentUser.getAccess_token())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CollectTopicResonse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(CollectTopicResonse collectTopicResonse) {
                            if (collectTopicResonse != null) {
                                Snackbar.make(mFabAddComment, "收藏成功", Snackbar.LENGTH_SHORT).show();

                                if (tvDetailCollect != null && mTopicEntity != null) {
                                    FavoriteUtil.addTopicToFavorite(TopicDetailActivity.this, mTopicEntity);
                                    tvDetailCollect.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bookmark_on, 0, 0, 0);
                                }
                            }
                        }
                    });
        }

    }

    boolean isPraised = false;

    @OnClick(R.id.tv_detail_praise)
    void onDetailPraiseClick() {

        if (mCurrentUser == null || TextUtils.isEmpty(mCurrentUser.getLogin())) {
            Snackbar.make(mFabAddComment, "请先登录客户端", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (PraiseUtil.hasPraised(TopicDetailActivity.this, mTopicId)) {
            RestAdapterUtils.getRestAPI(this)
                    .unLikeTopic(Config.PRAISE_TOPIC, mTopicId, mCurrentUser.getAccess_token())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PraiseEntity>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(PraiseEntity praiseEntity) {
                            if (praiseEntity != null) {
                                Snackbar.make(mFabAddComment, "取消点赞成功", Snackbar.LENGTH_SHORT).show();
                                PraiseUtil.delPraise(TopicDetailActivity.this, mTopicId);
                                if (tvDetailPraise != null) {
                                    tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_outline_grey, 0, 0, 0);
                                }
                            }

                        }
                    });
        } else {
            RestAdapterUtils.getRestAPI(this).praiseTopic(Config.PRAISE_TOPIC, mTopicId, mCurrentUser.getAccess_token())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<PraiseEntity>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(mFabAddComment, "Error:" + t.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(PraiseEntity praiseEntity) {
                            if (praiseEntity != null) {
                                Snackbar.make(mFabAddComment, "点赞成功", Snackbar.LENGTH_SHORT).show();
                                if (tvDetailPraise != null && mTopicEntity != null) {
                                    PraiseUtil.addPraise(TopicDetailActivity.this, mTopicEntity);
                                    tvDetailPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red, 0, 0, 0);
                                }
                            }

                        }
                    });
        }


    }

    @BindView(R.id.llAddComment)
    View mAddCommentPanel;

    @BindView(R.id.fab_add_comment)
    FloatingActionButton mFabAddComment;

    @OnClick(R.id.fab_add_comment)
    void onFabClick() {
        mAddCommentPanel.setVisibility(View.VISIBLE);
        mFabAddComment.setVisibility(View.GONE);
    }

    @BindView(R.id.etComment)
    EditText mEtComment;

    // 回帖子
    @OnClick(R.id.btnSendComment)
    void onSendCommentClick() {
        mEtComment.setError(null);


        if (mCurrentUser == null || TextUtils.isEmpty(mCurrentUser.getAccess_token())) {
            Snackbar.make(mFabAddComment,
                    "请先登录",
                    Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(mEtComment.getText().toString())) {
            mEtComment.setError("请输入回复内容");

        } else {
            String replyContent = mEtComment.getText().toString();
            String replyBody = "";
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(SettingsFragment.KEY_PREF_COMMENT_WITH_SNACK, true)) {
                replyBody = replyContent.concat("\n\n")
                        .concat("—— 来自TesterHome官方 [安卓客户端](http://fir.im/p9vs)");
            }


            final String finalReplyBody = replyContent;
            RestAdapterUtils.getRestAPI(this).createReply(mTopicId, replyBody, mCurrentUser.getAccess_token())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<CreateReplyResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable t) {
                            Snackbar.make(mFabAddComment,
                                    t.getMessage(),
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onNext(CreateReplyResponse response) {
                            if (response != null) {
                                // 发送成功
                                Log.d("detailActivity", "回帖成功");
                                mEtComment.setText("");
                                DeviceUtil.hideSoftInput(TopicDetailActivity.this);
                                mAddCommentPanel.setVisibility(View.GONE);
                                mTopicReplyFragment.updateReplyInfo(finalReplyBody);
                                // refresh reply count
                                if (response.getMeta() != null && response.getMeta().getCurrent_reply_count() > 0) {
                                    updateReplyCount(response.getMeta().getCurrent_reply_count() + 1);
                                }
                            }
                        }
                    });

        }
    }

    @Override
    public void updateReplyTo(String replyInfo) {
        if (mEtComment != null) {
            mAddCommentPanel.setVisibility(View.VISIBLE);
            mFabAddComment.setVisibility(View.GONE);
            mEtComment.setText(replyInfo);
            mEtComment.setSelection(mEtComment.getText().toString().length());
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddCommentPanel.getVisibility() == View.VISIBLE) {
            mAddCommentPanel.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}
