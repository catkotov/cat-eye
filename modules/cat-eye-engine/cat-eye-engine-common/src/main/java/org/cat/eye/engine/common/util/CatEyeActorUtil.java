package org.cat.eye.engine.common.util;

import org.cat.eye.engine.common.MsgTopic;

/**
 * Created by Kotov on 03.03.2019.
 */
public class CatEyeActorUtil {

    public static String getTopicName(String domain, MsgTopic topic) {
        return domain + "-" + topic.getTopicName();
    }
}
