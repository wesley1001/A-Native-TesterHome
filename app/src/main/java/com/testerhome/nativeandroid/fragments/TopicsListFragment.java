package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.testerhome.nativeandroid.BuildConfig;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicEntity;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 讨论节点列表
 * Created by Bin Li on 2015/9/16.
 */
public class TopicsListFragment extends BaseFragment implements Observer<TopicsResponse> {

    @BindView(R.id.rv_list)
    RecyclerView recyclerViewTopicList;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdapter;

    private String type;
    private int nodeId;


    public static TopicsListFragment newInstance(String type) {
        Log.d("TopicListFragment","newInstance");
        Bundle args = new Bundle();
        args.putString("type", type);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TopicsListFragment newInstance(int nodeId) {
        Bundle args = new Bundle();
        args.putInt("nodeId", nodeId);
        TopicsListFragment fragment = new TopicsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_base_recycler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getString("type");
        if (type == null) {
            nodeId = getArguments().getInt("nodeId");
        }

        loadTopics(true);
    }


    @Override
    protected void setupView() {
        mAdapter = new TopicsListAdapter(getActivity());
        mAdapter.setListener(() -> {
            if (mNextCursor > 0) {
                loadTopics(false);
            }
        });

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdapter);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mNextCursor = 0;
            loadTopics(false);
        });

    }

    private void loadTopics(boolean showloading) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showloading)
            showEmptyView();
        if (type != null) {
            mSubscription = RestAdapterUtils.getRestAPI(getActivity()).getTopicsByType(type,mNextCursor * 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        } else {
            mSubscription = RestAdapterUtils.getRestAPI(getActivity()).getTopicsByNodeId(nodeId, mNextCursor * 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (BuildConfig.DEBUG)
            Log.e("cache", "failure() called with: " + "error = [" + e.getMessage() + "]", e);
        if (mNextCursor == 0){
            showErrorView("无法加载数据,请检查网络");
        }
    }

    @Override
    public void onNext(TopicsResponse response) {
        hideEmptyView();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (response != null && response.getTopics().size() > 0) {
            if (mNextCursor == 0) {
                if (!TextUtils.isEmpty(type) && type.equals(Config.TOPICS_TYPE_RECENT)) {
                    response.getTopics().add(0, new TopicEntity(TopicsListAdapter.TOPIC_LIST_TYPE_BANNER, new ArrayList<>()));
                }
                mAdapter.setItems(response.getTopics());
            } else {
                mAdapter.addItems(response.getTopics());
            }
            if (response.getTopics().size() >= 20) {
                mNextCursor += 1;
            } else {
                mNextCursor = 0;
            }

        } else {
            if (mNextCursor == 0){
                showErrorView("无法加载数据,请检查网络");
            }
            mNextCursor = 0;
        }
    }
}
