package org.cat.eye.test.bundle.service;

import org.apache.ignite.client.IgniteClient;
import org.cat.eye.common.ignite.client.pool.CatEyeIgniteClientPool;
import org.cat.eye.engine.common.service.BundleService;

import java.lang.reflect.Parameter;

/**
 * Created by Kotov on 25.04.2019.
 */
public class IgniteBundleService implements BundleService {

    private CatEyeIgniteClientPool igniteClientPool;

    public IgniteBundleService() {
        init();
    }

    private void init() {
        this.igniteClientPool = new CatEyeIgniteClientPool(new String[] {"127.0.0.1:10800"}, 10 , 10, 10);
    }

    @Override
    public Object getArgument(Parameter parameter, String domain) throws Exception {

        IgniteClient client = igniteClientPool.getClient();
        Object obj = client.cache("argument").get(domain + "-" + parameter.getType().getName());
        igniteClientPool.releaseClient(client);

        return obj;
    }

    @Override
    public void setArgument(Parameter parameter, String domain, Object argument) throws Exception {
        IgniteClient client = igniteClientPool.getClient();
        client.cache("argument").put(domain + "-" + parameter.getType().getName(), argument);
        igniteClientPool.releaseClient(client);
    }

    @Override
    public void storeArguments(Object[] args, String domain) throws Exception {
        IgniteClient client = igniteClientPool.getClient();
        for (Object arg : args) {
            client.cache("argument").put(domain + "-" + arg.getClass().getName(), arg);
        }
        igniteClientPool.releaseClient(client);
    }
}
