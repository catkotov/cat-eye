package org.cat.eye.test.bundle.model;

import java.util.Set;

public interface FileCounterStore {

    long getFileNumber(String directoryName);

    void putFileNumber(String directoryName, long fileNumber);

    Set<String> getDirecotoryNames();
}
