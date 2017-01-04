package com.testerhome.nativeandroid.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.models.NotificationEntity;
import com.testerhome.nativeandroid.views.TopicDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhou.widget.RichText;

/**
 * Created by cvtpc on 2015/10/12.
 */
public class NotificationAdapter extends BaseAdapter<NotificationEntity> {

    private final int VIEW_SINGLE_ITEM = 1;
    private final int VIEW_DELETE_ITEM = 0;
    private final int VIEW_ITEM = 2;

    public static String TAG = "NotificationAdapter";

    public NotificationAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getActor() == null) {
            return VIEW_DELETE_ITEM;
        }
        switch (mItems.get(position).getType()) {
            case Config.FOLLOW:
            case Config.TOPIC:
                return VIEW_SINGLE_ITEM;
            case Config.TOPIC_REPLY:
            case Config.MENTION:
                return VIEW_ITEM;
            default:
                return VIEW_DELETE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_ITEM:
                view = View.inflate(parent.getContext(), R.layout.list_item_notification, null);
                return new NotificationHolder(view);
            case VIEW_SINGLE_ITEM:
                view = View.inflate(parent.getContext(), R.layout.list_single_item_notification, null);
                return new NotificationSingleHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item_delete_reply, parent, false);
                return new DeleteFloorHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        String userName;

        switch (getItemViewType(position)) {
            case VIEW_ITEM:
                NotificationHolder holder = (NotificationHolder) viewHolder;
                holder.notification = mItems.get(position);


                holder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(holder.notification.getActor().getAvatar_url())));
                userName = holder.notification.getActor().getLogin();
                if (holder.notification.getType().equals(Config.MENTION)) {
                    holder.notificationTitle.setText(ToDBC(userName + " 在帖子 " + mItems.get(position).getTopic().getTitle() + " 提及你:"));

                    String html = holder.notification.getMention().getBody_html();
                    html = html.replaceAll("src=\"/photo", "src=\"https://testerhome.com/photo");
                    html = html.replaceAll("src=\"/", "src=\"https://testerhome.com/");
                    html = html.replaceAll("/2/svg/", "/2/72x72/").replace(".svg", ".png");
                    ;
                    holder.notificationBody.setRichText(html);
                } else {
                    holder.notificationTitle.setText(ToDBC(userName + " 在帖子 " + mItems.get(position).getTopic().getTitle() + " 回复了:"));
                    String html = holder.notification.getReply().getBody_html();
                    html = html.replaceAll("src=\"/photo", "src=\"https://testerhome.com/photo");
                    html = html.replaceAll("src=\"/", "src=\"https://testerhome.com/");
                    html = html.replaceAll("/2/svg/", "/2/72x72/").replace(".svg", ".png");
                    holder.notificationBody.setRichText(html);

                }
                holder.mNotificationRow.setOnClickListener(
                        v -> mContext.startActivity(new Intent(mContext, TopicDetailActivity.class)
                                .putExtra("topic", mItems.get(position).getTopic())
                        ));
                break;
            case VIEW_SINGLE_ITEM:
                NotificationSingleHolder singleHolder = (NotificationSingleHolder) viewHolder;
                singleHolder.notification = mItems.get(position);
                userName = singleHolder.notification.getActor().getLogin();
                singleHolder.notification = mItems.get(position);
                singleHolder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(singleHolder.notification.getActor().getAvatar_url())));
                if (singleHolder.notification.getType().equals(Config.FOLLOW)) {
                    singleHolder.notificationTitle.setText(ToDBC(userName + mContext.getResources().getString(R.string.follow)));
                } else {
                    singleHolder.notificationTitle.setText(ToDBC(userName + "创建了一个新的帖子"));
                }
                break;
            case VIEW_DELETE_ITEM:
                DeleteFloorHolder deleteFloorHolder = (DeleteFloorHolder) viewHolder;
                deleteFloorHolder.topicItemBody.setText("该楼层已被删除");
                deleteFloorHolder.topicItemBody.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        }

        if (position == mItems.size() - 1 && mListener != null) {
            mListener.onListEnded();
        }

    }


//    private void loadViewHolder(RecyclerView.ViewHolder viewHolder, int position, String topicTitle) {
//        switch (getItemViewType(position)){
//            case VIEW_ITEM:
//                NotificationHolder holder = (NotificationHolder)viewHolder;
//                holder.notification = mItems.get(position);
//                holder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(holder.notification.getActor().getAvatar_url())));
//                String userName = holder.notification.getActor().getLogin();
//                if (holder.notification.getType().equals(Config.MENTION)) {
//                    holder.notificationTitle.setText(ToDBC(userName + " 在帖子 *** 提及你:"));
//                    holder.notificationBody.setText(Html.fromHtml(holder.notification.getMention().getBody_html()));
//                }else {
//                    holder.notificationTitle.setText(ToDBC(userName + " 在帖子 *** 回复了:"));
//                    holder.notificationBody.setText(Html.fromHtml(holder.notification.getReply().getBody_html()));
//                }
//
//                break;
//            case VIEW_SINGLE_ITEM:
//                NotificationSingleHolder singleHolder = (NotificationSingleHolder)viewHolder;
//                singleHolder.notification = mItems.get(position);
//                userName = singleHolder.notification.getActor().getLogin();
//                singleHolder.notification = mItems.get(position);
//                singleHolder.userAvatar.setImageURI(Uri.parse(Config.getImageUrl(singleHolder.notification.getActor().getAvatar_url())));
//                if (singleHolder.notification.getType().equals(Config.FOLLOW)) {
//                    singleHolder.notificationTitle.setText(ToDBC(userName + mContext.getResources().getString(R.string.follow)));
//                }else{
//                    singleHolder.notificationTitle.setText(ToDBC(userName + "创建了一个新的帖子"));
//                }
//                break;
//            case VIEW_DELETE_ITEM:
//                DeleteFloorHolder deleteFloorHolder = (DeleteFloorHolder) viewHolder;
//                deleteFloorHolder.topicItemBody.setText("该楼层已被删除");
//                deleteFloorHolder.topicItemBody.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//
//        }
//    }


    private EndlessListener mListener;

    public void setListener(EndlessListener mListener) {
        this.mListener = mListener;
    }

    public interface EndlessListener {
        void onListEnded();
    }

    private static class NotificationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_user_avatar)
        SimpleDraweeView userAvatar;

        @BindView(R.id.id_notification_title)
        TextView notificationTitle;

        @BindView(R.id.id_notification_body)
        RichText notificationBody;

        @BindView(R.id.notification_item_row)
        View mNotificationRow;

        NotificationEntity notification;

        NotificationHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private static class DeleteFloorHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_topic_item_content)
        TextView topicItemBody;

        DeleteFloorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private static class NotificationSingleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.id_user_avatar)
        SimpleDraweeView userAvatar;

        @BindView(R.id.id_notification_title)
        TextView notificationTitle;

        NotificationEntity notification;

        NotificationSingleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }


}
