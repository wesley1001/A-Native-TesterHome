package com.testerhome.nativeandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.views.adapters.CategoryAdapter;
import com.tonicartos.superslim.LayoutManager;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 讨论节点分类导航
 * Created by libin on 16/7/20.
 */
public class CategoryFragment extends BaseFragment {

    private static final String TAG = "CategoryFragment";

    @BindView(R.id.rv_list)
    RecyclerView mCategoryList;

    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private CategoryAdapter mAdapter;

    public static CategoryFragment newInstance() {

        Bundle args = new Bundle();

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadCategory(true);
    }

    @Override
    protected void setupView() {


        mAdapter = new CategoryAdapter(getActivity(), LayoutManager.LayoutParams.HEADER_STICKY | LayoutManager.LayoutParams.HEADER_INLINE);

        mCategoryList.setLayoutManager(new LayoutManager(getActivity()));
//        mCategoryList.addItemDecoration(new DividerItemDecoration(getActivity(),
//                DividerItemDecoration.VERTICAL_LIST));
        mCategoryList.setAdapter(mAdapter);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(() -> loadCategory(false));

    }

    private void loadCategory(boolean showLoading) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        if (showLoading)
            showEmptyView();

        mSubscription = RestAdapterUtils.getRestAPI(getContext()).getNodes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    hideEmptyView();
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setNodes(response.getNodes());
                }, error -> {
                    hideEmptyView();
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e(TAG, "loadCategory: " + Log.getStackTraceString(error));
                });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_base_recycler;
    }

}
