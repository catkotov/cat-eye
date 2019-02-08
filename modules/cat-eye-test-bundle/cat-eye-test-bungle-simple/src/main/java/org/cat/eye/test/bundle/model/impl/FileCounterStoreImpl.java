package org.cat.eye.test.bundle.model.impl;

import org.cat.eye.test.bundle.model.FileCounterStore;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileCounterStoreImpl implements FileCounterStore {

    private Map<String, Long> store = new ConcurrentHashMap<>();

    @Override
    public long getFileNumber(String directoryName) {
        return store.get(directoryName);
    }

    @Override
    public void putFileNumber(String directoryName, long fileNumber) {
        store.put(directoryName, fileNumber);
    }

    @Override
    public Set<String> getDirectoryNames() {
        return store.keySet();
    }

}
