package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.Compute;
import org.cat.eye.engine.model.annotation.IsComputable;
import org.cat.eye.engine.model.annotation.Out;
import org.cat.eye.test.bundle.model.impl.FileCounterStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@IsComputable
public class StartFileCounterComputer implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(StartFileCounterComputer.class);

    private final String path;

    private long filesTotalNumber;

    public StartFileCounterComputer(String path) {
        this.path = path;
    }

    @Compute
    public List<FileCounterComputer> getFileCounter() {

        List<FileCounterComputer> computers = new ArrayList<>();

        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            computers.add(new FileCounterComputer(directory.getAbsolutePath()));
            LOGGER.info("getFileCounter - STEP-1: Computation with path [{}} was created.", directory.getAbsolutePath());
        }

        return computers;
    }

    @Compute(step = 2)
    public List getNumberFilesInDirectory(@Out FileCounterStoreImpl store) throws Exception {

        if (store != null) {
            Set<String> dirSet = store.getDirectoryNames();
            if (dirSet != null && !dirSet.isEmpty()) {
                for (String dir : dirSet) {
                    long fileNumber = store.getFileNumber(dir);
                    filesTotalNumber += fileNumber;
                    LOGGER.info("getNumberFilesInDirectory - STEP-2: Directory [{}] contains [{}] files", dir, fileNumber);
                }
            }

            LOGGER.info("getNumberFilesInDirectory - STEP-2: Total number of files in directory [{}] is [{}].", path, filesTotalNumber);
        }

        return Collections.EMPTY_LIST;
    }
}
