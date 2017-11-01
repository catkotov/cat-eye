package org.cat.eye.test.bundle.simple;

import org.cat.eye.test.bundle.model.FileCounterStore;
import java.util.List;

public interface OtherFileCounter {

    List<OtherFileCounterComputer> getDirectoriesAndFileCounter();

    List countFilesInSubdirectories(FileCounterStore store);

    List countFiles(FileCounterStore store);
}
