package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.models.TopicsResponse;
import com.testerhome.nativeandroid.networks.TesterHomeApi;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vclub on 15/10/14.
 */
public class AccountTopicsFragment extends BaseFragment {

    @Bind(R.id.rv_topic_list)
    RecyclerView recyclerViewTopicList;

    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdatper;


    public static AccountTopicsFragment newInstance() {
        AccountTopicsFragment fragment = new AccountTopicsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mTesterHomeAccount == null) {
            getUserInfo();
        }
        loadTopics(true);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    protected void setupView() {
        mAdatper = new TopicsListAdapter(getActivity());
        mAdatper.setListener(new TopicsListAdapter.EndlessListener() {
            @Override
            public void onListEnded() {
                if (mNextCursor > 0) {
                    loadTopics(false);
                }
            }
        });

        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(mAdatper);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNextCursor = 0;
                loadTopics(false);
            }
        });

    }

    private TesterUser mTesterHomeAccount;

    private void getUserInfo() {
        mTesterHomeAccount = TesterHomeAccountService.getInstance(getContext()).getActiveAccountInfo();
    }


    private void loadTopics(boolean showloading) {
        if (showloading)
            showLoadingView();

        TesterHomeApi.getInstance().getTopicsService().getUserTopics(mTesterHomeAccount.getLogin(),
                mTesterHomeAccount.getAccess_token(),
                mNextCursor*20,
                new Callback<TopicsResponse>() {
                    @Override
                    public void success(TopicsResponse topicsResponse, Response response) {
                        hideLoadingView();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (topicsResponse.getTopics().size() > 0) {

                            if (mNextCursor == 0) {
                                mAdatper.setItems(topicsResponse.getTopics());
                            } else {
                                mAdatper.addItems(topicsResponse.getTopics());
                            }

                            if (topicsResponse.getTopics().size() == 20) {
                                mNextCursor += 1;
                            } else {
                                mNextCursor = 0;
                            }
                        } else {
                            mNextCursor = 0;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideLoadingView();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Log.e("demo", "failure() called with: " + "error = [" + error + "]"
                                + error.getUrl());
                    }
                });
    }
}
