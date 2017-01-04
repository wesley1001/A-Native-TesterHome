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
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.TopicsListAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vclub on 15/10/14.
 */
public class AccountTopicsFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView recyclerViewTopicList;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private TopicsListAdapter mAdatper;
    private String loginName;

    public static AccountTopicsFragment newInstance(String loginName) {
        Bundle bundle = new Bundle();
        bundle.putString("loginName",loginName);
        AccountTopicsFragment fragment = new AccountTopicsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_base_recycler;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mTesterHomeAccount == null) {
            getUserInfo();
        }


        loginName = getArguments().getString("loginName");
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
            showEmptyView();


        RestAdapterUtils.getRestAPI(getActivity()).getUserTopics(loginName,
                mTesterHomeAccount.getAccess_token(), mNextCursor * 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TopicsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable t) {
                        hideEmptyView();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Log.e("demo", "failure() called with: " + "error = [" + t + "]"
                                , t);
                    }

                    @Override
                    public void onNext(TopicsResponse response) {
                        hideEmptyView();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (response != null && response.getTopics().size() > 0) {
                            if (mNextCursor == 0) {
                                mAdatper.setItems(response.getTopics());
                            } else {
                                mAdatper.addItems(response.getTopics());
                            }

                            if (response.getTopics().size() == 20) {
                                mNextCursor += 1;
                            } else {
                                mNextCursor = 0;
                            }
                        } else {
                            mNextCursor = 0;
                        }
                    }
                });

    }
}
