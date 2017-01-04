package com.testerhome.nativeandroid.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;

/**
 * Created by libin on 16/7/20.
 */
public class NodeViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;

    public NodeViewHolder(View itemView) {
        super(itemView);

        mTextView = (TextView)itemView.findViewById(R.id.text);
    }

    public void bindItem(String text, View.OnClickListener listener){
        mTextView.setText(text);
        mTextView.setOnClickListener(listener);
    }

    @Override
    public String toString() {
        return mTextView.getText().toString();
    }
}
