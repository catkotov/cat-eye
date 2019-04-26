package org.cat.eye.test.bundle.model;

import java.util.Set;

public interface FileCounterStore {

    long getFileNumber(String directoryName) throws Exception;

    void putFileNumber(String directoryName, long fileNumber) throws Exception;

    Set<String> getDirectoryNames() throws Exception;
}
