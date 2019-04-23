package org.cat.eye.common.ignite.client.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;

/**
 * Created by Kotov on 20.04.2019.
 */
public class CatEyeIgniteClientPoolFactory extends BasePooledObjectFactory<IgniteClient> {

    private String[] addresses;

    public CatEyeIgniteClientPoolFactory(String[] addresses) {
        this.addresses = addresses;
    }

    @Override
    public IgniteClient create() throws Exception {
        return Ignition.startClient(new ClientConfiguration().setAddresses(addresses));
    }

    @Override
    public PooledObject<IgniteClient> wrap(IgniteClient igniteClient) {
        return new DefaultPooledObject<>(igniteClient);
    }

    @Override
    public void destroyObject(PooledObject<IgniteClient> pooledObject) throws Exception {
        pooledObject.getObject().close();
    }
}
