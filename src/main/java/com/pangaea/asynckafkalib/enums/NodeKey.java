package com.pangaea.asynckafkalib.enums;

public enum NodeKey {

    PUBLISHER_TOPIC("topic"),
    PUBLISHER_DATA("data"),
    PUBLISHER_NEWSLETTER("newsletter");

    private String nodeKey;

    NodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getNodeKey() {
        return nodeKey;
    }
}
