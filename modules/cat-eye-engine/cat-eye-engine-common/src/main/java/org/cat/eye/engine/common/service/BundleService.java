package org.cat.eye.engine.common.service;

import java.lang.reflect.Parameter;

public interface BundleService {

    Object getArgument(Parameter parameter, String domain) throws Exception;

    void setArgument(Parameter parameter, String domain, Object argument) throws Exception;

    void storeArguments(Object[] args, String domain) throws Exception;

}
