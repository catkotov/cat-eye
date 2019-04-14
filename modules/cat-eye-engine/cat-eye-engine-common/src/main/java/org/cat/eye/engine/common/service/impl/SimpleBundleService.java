package org.cat.eye.engine.common.service.impl;

import org.cat.eye.engine.common.service.BundleService;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 14.04.2019.
 */
public class SimpleBundleService implements BundleService {

    private Map<String, Object> argumentStore = new ConcurrentHashMap<>();

    @Override
    public Object getArgument(Parameter parameter, String domain) {
        return this.argumentStore.get(domain + "-" + parameter.getType().getName());
    }

    @Override
    public void setArgument(Parameter parameter, String domain, Object argument) {
        this.argumentStore.put(domain + "-" + parameter.getType().getName(), argument);
    }

    @Override
    public void storeArguments(Object[] args, String domain) {
        for (Object arg : args) {
            this.argumentStore.put(domain + "-" + arg.getClass().getName(), arg);
        }
    }

}
