package org.cat.eye.test.bundle.simple;

import org.cat.eye.test.bundle.model.FileCounterStore;
import org.cat.eye.test.bundle.model.impl.FileCounterStoreImpl;

import java.util.List;

public interface OtherFileCounter {

    List<OtherFileCounterComputer> getDirectoriesAndFileCounter();

    List countFilesInSubdirectories(FileCounterStoreImpl store);

    List countFiles(FileCounterStoreImpl store);
}
