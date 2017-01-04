package com.testerhome.nativeandroid.models;

import java.util.List;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class SearchResponse {

    private List<TopicEntity> search;

    public List<TopicEntity> getTopics() {
        return search;
    }

    public void setTopics(List<TopicEntity> search) {
        this.search = search;
    }
}
