package org.cat.eye.test.bundle.model.impl;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.cat.eye.test.bundle.model.FileCounterStore;

import javax.cache.Cache;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileCounterStoreImpl implements FileCounterStore {

    private static final String STORE_CACHE_NAME = "storeCache";
    // TODO get from outside
    private String[] addresses = {"127.0.0.1:47500..47509"};

    private IgniteCache<String, Long> storeCache;

    public FileCounterStoreImpl() {

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(addresses));

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(ipFinder);

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setDiscoverySpi(spi);

        cfg.setPeerClassLoadingEnabled(true);
        //Client mode is ON
        cfg.setClientMode(true);

        CacheConfiguration<String, Long> storeCacheCfg = new CacheConfiguration<>();
        storeCacheCfg.setName(STORE_CACHE_NAME);
        storeCacheCfg.setCacheMode(CacheMode.PARTITIONED);
        storeCacheCfg.setBackups(1);
        storeCacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);

        cfg.setCacheConfiguration(storeCacheCfg);

        Ignite ignite = Ignition.start(cfg);

        storeCache = ignite.getOrCreateCache(STORE_CACHE_NAME);
    }

    @Override
    public long getFileNumber(String directoryName) {
        return storeCache.get(directoryName);
    }

    @Override
    public void putFileNumber(String directoryName, long fileNumber) {
        storeCache.put(directoryName, fileNumber);
    }

    @Override
    public Set<String> getDirectoryNames() {

        Set<String> result = new HashSet<>();

        try (QueryCursor<Cache.Entry<String, Long>> cursor =
                     storeCache.query(new ScanQuery<>((k, v) -> v > 0))) {
            for (Cache.Entry<String, Long> entry : cursor)
                result.add(entry.getKey());
        }

        return result;
    }

}
