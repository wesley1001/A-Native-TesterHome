package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.TopicReplyEntity;
import com.testerhome.nativeandroid.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhou.widget.RichText;

/**
 * Created by cvtpc on 2015/10/16.
 */
public class TopicReplyAdapter extends BaseAdapter<TopicReplyEntity> {

    public static String TAG = "TopicReplyAdapter";
    private final int VIEW_ITEM = 1;
    private final int VIEW_DELETE_ITEM = 0;
    private Context context;

    public TopicReplyAdapter(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isDeleted() ? VIEW_DELETE_ITEM : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = View.inflate(parent.getContext(), R.layout.list_item_reply, null);
            return new ReplyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item_delete_reply, parent, false);
            return new DeleteFloorHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        if (viewHolder instanceof ReplyViewHolder) {
            TopicReplyEntity topicReplyEntity = mItems.get(position);
            String html = topicReplyEntity.getBody_html();

            if(html == null) {
                return;
            }

            ReplyViewHolder holder = (ReplyViewHolder) viewHolder;
            holder.praiseReplyLayout.setVisibility(View.VISIBLE);
            holder.userAvatar.setVisibility(View.VISIBLE);
            holder.topicItemAuthor.setVisibility(View.VISIBLE);
            holder.topicTime.setVisibility(View.VISIBLE);
            holder.topicTime.setText(StringUtils.formatPublishDateTime(topicReplyEntity.getCreated_at()));
            holder.topicItemAuthor.setText(TextUtils.isEmpty(topicReplyEntity.getUser().getName()) ?
                    topicReplyEntity.getUser().getLogin() : topicReplyEntity.getUser().getName());

            // https://twemoji.b0.upaiyun.com/2/72x72/1f3c0.png
            // <img src="/uploads/photo/2016/8eef4d18673bd20ddc3f338e43b673a2.png!large" width="300px" alt="">
            // <img title=":joy_cat:" alt="😹" src="https://twemoji.b0.upaiyun.com/2/72x72/1f639.png" class="twemoji">
            html = html.replaceAll("src=\"/", "src=\"https://testerhome.com/");
            html = html.replaceAll("/2/svg/", "/2/72x72/").replace(".svg", ".png");

            Log.d(TAG, "onBindViewHolder: "+ html);

            holder.topicItemBody.setRichText(html);
            holder.topicItemBody.setMovementMethod(LinkMovementMethod.getInstance());

            holder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(topicReplyEntity.getUser().getAvatar_url())));

            holder.mToReply.setTag(String.format("#%s楼 @%s ", position + 1, topicReplyEntity.getUser().getLogin()));
            holder.mToReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onReplyClick((String) v.getTag());
                    }
                }
            });
        } else {
            DeleteFloorHolder holder = (DeleteFloorHolder) viewHolder;
            holder.topicItemBody.setText("该楼层已被删除");
            holder.topicItemBody.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
        }

        if (position == mItems.size() - 1 && mListener != null) {
            mListener.onListEnded();
        }
    }

    private TopicReplyListener mListener;

    public void setListener(TopicReplyListener mListener) {
        this.mListener = mListener;
    }

    public interface TopicReplyListener {
        void onListEnded();

        void onReplyClick(String replyInfo);
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_praise_reply_layout)
        LinearLayout praiseReplyLayout;

        @BindView(R.id.id_topic_item_author)
        TextView topicItemAuthor;

        @BindView(R.id.id_topic_item_content)
        RichText topicItemBody;
        @BindView(R.id.id_topic_time)
        TextView topicTime;

        @BindView(R.id.id_user_avatar)
        SimpleDraweeView userAvatar;

        @BindView(R.id.tv_reply_to_reply)
        TextView mToReply;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class DeleteFloorHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.id_topic_item_content)
        TextView topicItemBody;

        public DeleteFloorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
