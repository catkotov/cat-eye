package org.cat.eye.common.ignite.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.ignite.client.IgniteClient;

/**
 * Created by Kotov on 20.04.2019.
 */
public class CatEyeIgniteClientPool {

    private GenericObjectPool<IgniteClient> igniteClientPool;

    public CatEyeIgniteClientPool(String[] addresses, int maxTotal, int maxIdle, int minIdle) {

        GenericObjectPoolConfig<IgniteClient> igniteClientPoolConfig = new GenericObjectPoolConfig<>();
        igniteClientPoolConfig.setMaxIdle(maxIdle);
        igniteClientPoolConfig.setMaxTotal(maxTotal);
        igniteClientPoolConfig.setMinIdle(minIdle);

        this.igniteClientPool =
                new GenericObjectPool<>(new CatEyeIgniteClientPoolFactory(addresses), igniteClientPoolConfig);
    }

    public IgniteClient getClient() throws Exception {
        return igniteClientPool.borrowObject();
    }

    public void releaseClient(IgniteClient igniteClient) {
        igniteClientPool.returnObject(igniteClient);
    }

    public void close() {
        igniteClientPool.close();
    }
}
