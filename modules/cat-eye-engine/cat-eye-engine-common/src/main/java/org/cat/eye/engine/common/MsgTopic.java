package org.cat.eye.engine.common;

public enum MsgTopic {
    NEW_COMPUTATION("new-computation"),
    COMPLETED_COMPUTATION("completed-computation"),
    RUNNABLE_COMPUTATION("runnable-computation"),
    RUNNING_COMPUTATION("running-computation");

    String topicName;

    MsgTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return this.topicName;
    }
}
