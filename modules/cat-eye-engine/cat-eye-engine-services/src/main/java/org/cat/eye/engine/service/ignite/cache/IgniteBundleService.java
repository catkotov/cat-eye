package org.cat.eye.engine.service.ignite.cache;

import org.apache.ignite.client.IgniteClient;
import org.cat.eye.common.ignite.client.pool.CatEyeIgniteClientPool;
import org.cat.eye.engine.common.service.BundleService;

import java.lang.reflect.Parameter;

/**
 * Created by Kotov on 25.04.2019.
 */
public class IgniteBundleService implements BundleService {

    CatEyeIgniteClientPool igniteClientPool;

    public IgniteBundleService(String[] addresses, int maxTotal, int maxIdle, int minIdle) {
        this.igniteClientPool = new CatEyeIgniteClientPool(addresses, maxTotal, maxIdle, minIdle);
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
