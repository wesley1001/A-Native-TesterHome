package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jauker.widget.BadgeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.ToutiaoResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.utils.DeviceUtil;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.TopicDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListAdapter extends BaseAdapter<TopicEntity> {

    public static final int TOPIC_LIST_TYPE_BANNER = 0;
    public static final int TOPIC_LIST_TYPE_TOPIC = 1;
    private Context context;
    private View[] imageViews;

    public TopicsListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {
            case TOPIC_LIST_TYPE_BANNER:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_banner, parent, false);
                ViewGroup.LayoutParams bannerLayoutParams = view.findViewById(R.id.banner_layout).getLayoutParams();
                bannerLayoutParams.width = DeviceUtil.getDeviceWidth(mContext);
                bannerLayoutParams.height = (bannerLayoutParams.width / 2);
                view.findViewById(R.id.banner_layout).setLayoutParams(bannerLayoutParams);

                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.view_group);
                loadToutiao(viewGroup);
                return new TopicBannerViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_topic, parent, false);
                return new TopicItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    private TopicBannerAdapter mBannerAdapter;
    private TextView mTopicBannerTitle;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (getItemViewType(position)) {
            case TOPIC_LIST_TYPE_BANNER:
                TopicBannerViewHolder bannerViewHolder = (TopicBannerViewHolder) viewHolder;
                if (mBannerAdapter == null) {
                    mBannerAdapter = new TopicBannerAdapter();
                }
                bannerViewHolder.mTopicBanner.setAdapter(mBannerAdapter);
                startPlayNext(bannerViewHolder.mTopicBanner);
                mTopicBannerTitle = bannerViewHolder.mTopicBannerTitle;
                break;
            default:
                TopicItemViewHolder holder = (TopicItemViewHolder) viewHolder;

                TopicEntity topic = mItems.get(position);

                holder.topicUserAvatar.setImageURI(Uri.parse(Config.getImageUrl(topic.getUser().getAvatar_url())));
                holder.textViewTopicTitle.setText(topic.getTitle());
                holder.topicUsername.setText(TextUtils.isEmpty(topic.getUser().getName()) ? topic.getUser().getLogin() : topic.getUser().getName());

                holder.topicPublishDate.setText(StringUtils.formatPublishDateTime(topic.getCreated_at()));

                holder.topicName.setText(topic.getNode_name());

                holder.badgeView.setTargetView(holder.topicRepliesCount);
                holder.badgeView.setHideOnNull(false);
                holder.badgeView.setBadgeCount(topic.getReplies_count());
                holder.topicItem.setTag(topic.getId());
                holder.topicItem.setOnClickListener(v -> {
                    if (DeviceUtil.isFastClick()) {
                        return;
                    }
                    String topicId = (String) v.getTag();
                    mContext.startActivity(new Intent(mContext, TopicDetailActivity.class)
                            .putExtra("topic", topic));
                });
                break;
        }

        if (position == mItems.size() - 1 && mListener != null) {
            mListener.onListEnded();
        }
    }

    private void startPlayNext(ViewPager banner){
        banner.postDelayed(() -> {
            int cur = banner.getCurrentItem() + 1;
            if (cur>= banner.getAdapter().getCount()){
                cur = 0;
            }
            banner.setCurrentItem(cur);

            if (banner.getWindowToken() != null)
                startPlayNext(banner);
        }, 3000);
    }

    private EndlessListener mListener;

    public void setListener(EndlessListener mListener) {
        this.mListener = mListener;
    }

    public interface EndlessListener {
        void onListEnded();
    }

    public class TopicItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sdv_topic_user_avatar)
        SimpleDraweeView topicUserAvatar;

        @BindView(R.id.tv_topic_title)
        TextView textViewTopicTitle;

        @BindView(R.id.tv_topic_username)
        TextView topicUsername;

        @BindView(R.id.tv_topic_publish_date)
        TextView topicPublishDate;

        @BindView(R.id.tv_topic_name)
        TextView topicName;

        @BindView(R.id.tv_topic_replies_count)
        TextView topicRepliesCount;

        @BindView(R.id.rl_topic_item)
        View topicItem;

        BadgeView badgeView;

        public TopicItemViewHolder(View itemView) {
            super(itemView);

            badgeView = new BadgeView(itemView.getContext());
            ButterKnife.bind(this, itemView);
        }
    }

    public class TopicBannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vp_topic_banner)
        ViewPager mTopicBanner;

        @BindView(R.id.tv_banner_title)
        TextView mTopicBannerTitle;


        public TopicBannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mTopicBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    initPoint();
                    if (mTopicBannerTitle != null) {
                        mTopicBannerTitle.setText(mBannerAdapter.getPageTitle(position));
                        imageViews[position].setBackgroundResource(R.color.colorAccent);
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }

    private void initPoint() {
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setBackgroundResource(R.color.tab_item_tint_default);
        }
    }

    private static final String TAG = "TopicsListAdapter";

    // region
    private void loadToutiao(final ViewGroup viewGroup) {

        RestAdapterUtils.getRestAPI(mContext).getToutiao()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ToutiaoResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(ToutiaoResponse toutiaoResponse) {
                        if (toutiaoResponse != null) {
                            imageViews = new View[toutiaoResponse.getAds().size()];
                            for (int i = 0; i < imageViews.length; i++) {
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
                                layoutParams.weight = 1;

                                View imageView = new View(context);
                                imageViews[i] = imageView;
                                if (i == 0) {
                                    imageViews[i].setBackgroundResource(R.color.colorAccent);
                                } else {
                                    imageViews[i].setBackgroundResource(R.color.tab_item_tint_default);
                                }
                                viewGroup.addView(imageViews[i], layoutParams);
                            }

                            if (toutiaoResponse.getAds().size() > 0) {
                                mBannerAdapter.setItems(toutiaoResponse.getAds());
                                if (mBannerAdapter != null)
                                    mTopicBannerTitle.setText(mBannerAdapter.getPageTitle(0));
                            } else {
                                // no info
                            }
                        }
                    }
                });
    }
    // endregion
}
