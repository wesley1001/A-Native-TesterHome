package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.SearchResponse;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bin Li on 2015/9/16.
 */
public class SearchResultFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView recyclerViewTopicList;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 1;

    private TopicsListAdapter mAdatper;

    private String keyword;

    public static SearchResultFragment newInstance(String keyword) {
        Bundle args = new Bundle();
        args.putString("keyword", keyword);
        SearchResultFragment fragment = new SearchResultFragment();
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
        keyword = getArguments().getString("keyword");

        loadTopics(true);
    }


    @Override
    protected void setupView() {
        mAdatper = new TopicsListAdapter(getActivity());
        mAdatper.setListener(new TopicsListAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 1) {
                    loadTopics(false);
                }
            }
        });

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 1;
                loadTopics(false);
            }
        });

    }

    private void loadTopics(boolean showloading) {

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showloading)
            showEmptyView();


        if (keyword != null) {
            mSubscription = RestAdapterUtils.getRestAPI(getActivity())
                    .searchTopicsByKeyword(keyword, mNextCursor * 20)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObservable);
        }

    }

    Observer<SearchResponse> mObservable = new Observer<SearchResponse>() {

        @Override
        public void onCompleted() {
            hideEmptyView();

            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Throwable e) {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onNext(SearchResponse response) {

            if (response != null && response.getTopics().size() > 0) {
                if (mNextCursor == 1) {
                    mAdatper.setItems(response.getTopics());
                } else {
                    mAdatper.addItems(response.getTopics());
                }
                if (response.getTopics().size() >= 10) {
                    mNextCursor += 1;
                } else {
                    mNextCursor = 1;
                }

            } else {
                mNextCursor = 1;
            }
        }
    };

}