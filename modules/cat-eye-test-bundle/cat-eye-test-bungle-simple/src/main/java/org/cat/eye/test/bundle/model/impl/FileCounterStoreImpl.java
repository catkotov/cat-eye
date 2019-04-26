package org.cat.eye.test.bundle.model.impl;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientCacheConfiguration;
import org.apache.ignite.client.IgniteClient;
import org.cat.eye.common.ignite.client.pool.CatEyeIgniteClientPool;
import org.cat.eye.test.bundle.model.FileCounterStore;
import javax.cache.Cache;
import java.util.HashSet;
import java.util.Set;

public class FileCounterStoreImpl implements FileCounterStore {

    private static final String STORE_CACHE_NAME = "storeCache";
    // TODO get from outside
    private String[] addresses = {"127.0.0.1:10800"};

    private CatEyeIgniteClientPool igniteClientPool;

    public FileCounterStoreImpl() throws Exception {

        this.igniteClientPool = new CatEyeIgniteClientPool(addresses, 10, 10, 10);

        ClientCacheConfiguration storeCacheCfg = new ClientCacheConfiguration();
        storeCacheCfg.setName(STORE_CACHE_NAME);
        storeCacheCfg.setCacheMode(CacheMode.PARTITIONED);
        storeCacheCfg.setBackups(1);
        storeCacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        IgniteClient client = igniteClientPool.getClient();

        client.getOrCreateCache(storeCacheCfg);

        igniteClientPool.releaseClient(client);
    }

    @Override
    public long getFileNumber(String directoryName) throws Exception {

        IgniteClient client = igniteClientPool.getClient();
        ClientCache<String, Long> storeCache = client.cache(STORE_CACHE_NAME);
        long result = storeCache.get(directoryName);
        igniteClientPool.releaseClient(client);
        return result;
    }

    @Override
    public void putFileNumber(String directoryName, long fileNumber) throws Exception {
        IgniteClient client = igniteClientPool.getClient();
        ClientCache<String, Long> storeCache = client.cache(STORE_CACHE_NAME);
        storeCache.put(directoryName, fileNumber);
        igniteClientPool.releaseClient(client);
    }

    @Override
    public Set<String> getDirectoryNames() throws Exception {

        Set<String> result = new HashSet<>();

        IgniteClient client = igniteClientPool.getClient();
        ClientCache<String, Long> storeCache = client.cache(STORE_CACHE_NAME);

        try (QueryCursor<Cache.Entry<String, Long>> cursor =
                     storeCache.query(new ScanQuery<>((k, v) -> v > 0))) {
            for (Cache.Entry<String, Long> entry : cursor)
                result.add(entry.getKey());
        }

        igniteClientPool.releaseClient(client);

        return result;
    }

}
