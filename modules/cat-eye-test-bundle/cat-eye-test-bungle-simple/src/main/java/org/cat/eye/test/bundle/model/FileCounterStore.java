package org.cat.eye.test.bundle.model;

public interface FileCounterStore {

    long getFileNumber(String directoryName);

    void putFileNumber(String directoryName, long fileNumber);
}
