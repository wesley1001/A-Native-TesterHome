package com.testerhome.nativeandroid.storage;

/**
 * Created by cvter on 12/3/16.
 */
import android.content.Context;

import com.testerhome.nativeandroid.utils.SharedPreferencesHelper;

public final class TopicShared {

    private TopicShared() {}

    private static final String TAG = "TopicShared";

    private static final String KEY_NEW_TOPIC_TAB_POSITION = "new_topic_tab_position";
    private static final String KEY_NEW_TOPIC_TITLE = "new_topic_title";
    private static final String KEY_NEW_TOPIC_CONTENT = "new_topic_content";


    public static int getNewTopicTabPosition(Context context) {

        return SharedPreferencesHelper.getInstance(context).getIntValue(KEY_NEW_TOPIC_TAB_POSITION);
    }

    public static void setNewTopicTabPosition(Context context, int position) {
        SharedPreferencesHelper.getInstance(context).setValue(KEY_NEW_TOPIC_TAB_POSITION, position);
    }

    public static String getNewTopicTitle(Context context) {
        return SharedPreferencesHelper.getInstance(context).getValue(KEY_NEW_TOPIC_TITLE);
    }

    public static void setNewTopicTitle(Context context, String title) {
        SharedPreferencesHelper.getInstance(context).setValue(KEY_NEW_TOPIC_TITLE, title);
    }

    public static String getNewTopicContent(Context context) {
        return SharedPreferencesHelper.getInstance(context).getValue(KEY_NEW_TOPIC_CONTENT);
    }

    public static void setNewTopicContent(Context context, String content) {
        SharedPreferencesHelper.getInstance(context).setValue(KEY_NEW_TOPIC_CONTENT, content);
    }

    public static void clearNewTopic(Context context) {
        SharedPreferencesHelper.getInstance(context).remove(KEY_NEW_TOPIC_TAB_POSITION);
        SharedPreferencesHelper.getInstance(context).remove(KEY_NEW_TOPIC_TITLE);
        SharedPreferencesHelper.getInstance(context).remove(KEY_NEW_TOPIC_CONTENT);
    }



}