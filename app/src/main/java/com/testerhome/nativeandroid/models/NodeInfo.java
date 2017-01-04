package com.testerhome.nativeandroid.models;

/**
 * 讨论节点信息
 * Created by libin on 16/7/20.
 */
public class NodeInfo {


    /**
     * id : 1
     * name : 资讯点评
     * topics_count : 83
     * summary : 测试最新资讯点评
     * section_id : 2
     * sort : 0
     * section_name : 测试资料
     * updated_at : 2013-12-12T16:30:28.041+08:00
     */

    private int id;
    private String name;
    private int topics_count;
    private String summary;
    private int section_id;
    private int sort;
    private String section_name;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTopics_count() {
        return topics_count;
    }

    public void setTopics_count(int topics_count) {
        this.topics_count = topics_count;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
