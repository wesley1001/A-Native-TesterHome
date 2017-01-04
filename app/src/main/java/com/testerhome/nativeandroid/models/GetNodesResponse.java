package com.testerhome.nativeandroid.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点列表
 * Created by libin on 16/7/20.
 */
public class GetNodesResponse {

    private List<NodeInfo> nodes;

    public GetNodesResponse() {
        this.nodes = new ArrayList<>();
    }

    public List<NodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeInfo> nodes) {
        this.nodes = nodes;
    }
}
