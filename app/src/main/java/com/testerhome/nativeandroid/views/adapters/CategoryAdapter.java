package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.NodeInfo;
import com.testerhome.nativeandroid.views.TopicListActivity;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分类节点 Adapter
 * Created by libin on 16/7/20.
 */
public class CategoryAdapter extends RecyclerView.Adapter<NodeViewHolder> {

    private Context mContext;
    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int LINEAR = 0;

    private final ArrayList<LineItem> mItems;

    private int mHeaderDisplay;

    private boolean mMarginsFixed;

    public CategoryAdapter(Context context, int headerMode) {
        mContext = context;
        mItems = new ArrayList<>();

        mHeaderDisplay = headerMode;
    }


    @Override
    public NodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_line_item, parent, false);
        }
        return new NodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NodeViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        if (item.isHeader) {
            holder.bindItem(item.node.getSection_name(), null);

            lp.headerDisplay = mHeaderDisplay;
            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;

        } else {
            holder.bindItem(item.node.getName(),
                    view -> mContext.startActivity(new Intent(mContext, TopicListActivity.class)
                            .putExtra("title", item.node.getName())
                            .putExtra("nodeId", item.node.getId())));
        }

        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }


    public void setNodes(List<NodeInfo> nodes) {
        ArrayList<LineItem> items = new ArrayList<>();
        //Insert headers into list of items.
        String lastHeader = "";
        int headerCount = 0;
        int sectionFirstPosition = 0;
        for (int i = 0; i < nodes.size(); i++) {
            String header = nodes.get(i).getSection_name();
            if (!TextUtils.equals(lastHeader, header)) {
                // Insert new header view and update section data.
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                items.add(new LineItem(nodes.get(i), true, sectionFirstPosition));
            }
            items.add(new LineItem(nodes.get(i), false, sectionFirstPosition));
        }

        mItems.clear();
        mItems.addAll(items);
        items = null;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            LineItem item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }


    private static class LineItem {

        public int sectionFirstPosition;

        public boolean isHeader;

        public NodeInfo node;

        public LineItem(NodeInfo node, boolean isHeader, int sectionFirstPosition) {
            this.isHeader = isHeader;
            this.node = node;
            this.sectionFirstPosition = sectionFirstPosition;
        }
    }
}
