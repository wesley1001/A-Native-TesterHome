package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.NotificationResponse;
import com.testerhome.nativeandroid.models.TesterUser;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.NotificationAdapter;
import com.testerhome.nativeandroid.views.widgets.DividerItemDecoration;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cvtpc on 2015/10/18.
 */
public class AccountNotificationFragment extends BaseFragment {

    @BindView(R.id.rv_list)
    RecyclerView recyclerViewTopicList;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private int mNextCursor = 0;

    private NotificationAdapter notificationAdapter;


    public static AccountNotificationFragment newInstance() {
        return new AccountNotificationFragment();
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
        setupView();
        loadNotification(true);

    }

    @Override
    protected void setupView() {
        notificationAdapter = new NotificationAdapter(getActivity());
        notificationAdapter.setListener(() -> {
            if (mNextCursor > 0) {
                loadNotification(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        recyclerViewTopicList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTopicList.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerViewTopicList.setAdapter(notificationAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mNextCursor = 0;
            loadNotification(false);
        });


    }

    private TesterUser mTesterHomeAccount;

    private void getUserInfo() {
        mTesterHomeAccount = TesterHomeAccountService.getInstance(getContext()).getActiveAccountInfo();
    }


    private void loadNotification(boolean showloading) {
        if (showloading)
            showEmptyView();

        RestAdapterUtils.getRestAPI(getActivity())
                .getNotifications(mTesterHomeAccount.getAccess_token(), mNextCursor * 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NotificationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable t) {
                        hideEmptyView();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Log.e("demo", "failure() called with: " + "error = [" + t.getMessage() + "]", t);
                    }

                    @Override
                    public void onNext(NotificationResponse notificationResponse) {
                        if (notificationResponse != null) {
                            hideEmptyView();
                            if (swipeRefreshLayout !=null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            if (notificationResponse.getNotifications().size() > 0) {
                                if (mNextCursor == 0) {
                                    notificationAdapter.setItems(notificationResponse.getNotifications());
                                } else {
                                    notificationAdapter.addItems(notificationResponse.getNotifications());
                                }
                                if (notificationResponse.getNotifications().size() == 20) {
                                    mNextCursor += 1;
                                } else {
                                    mNextCursor = 0;
                                }

                            } else {
                                mNextCursor = 0;
                            }
                        }
                    }
                });



    }


}
