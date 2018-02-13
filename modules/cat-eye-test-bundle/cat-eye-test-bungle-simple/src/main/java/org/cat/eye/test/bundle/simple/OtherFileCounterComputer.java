package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.*;
import org.cat.eye.test.bundle.model.impl.FileCounterStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@IsComputable
public class OtherFileCounterComputer {

    private final static Logger LOGGER = LoggerFactory.getLogger(OtherFileCounterComputer.class);

    @Id
    private final String path;

    private List<OtherFileCounterComputer> computers = new ArrayList<>();

    private long numberSubdirectoryFiles;
    // number of files in directory
    private int numberLocalFiles;

    private long numberFiles;

    public OtherFileCounterComputer(String path) {
        this.path = path;
    }

    private String getPath() {
        return path;
    }

    @Compute
    public List<OtherFileCounterComputer> getDirectoriesAndFileCounter() {

        int fileCount = 0;

        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        computers.add(new OtherFileCounterComputer(file.getAbsolutePath()));
                        LOGGER.info("getDirectoriesAndFileCounter - " +
                                "STEP-1: Computation with path [{}} was created.", file.getAbsolutePath());
                    } else {
                        fileCount++;
                    }
                }
            }
        }

        // write result
        numberLocalFiles = fileCount;

        LOGGER.info("getDirectoriesAndFileCounter - STEP-1: Directory [{}] contains [{}] local files.", path, numberLocalFiles);

        return computers;
    }

    @Compute(step = 2)
    public List countFilesInSubdirectories(@In FileCounterStoreImpl store) {

        long counter = 0;

        if (!this.computers.isEmpty()) {
            for (OtherFileCounterComputer computer : computers) {
                counter =+ computer.getNumberLocalFiles();

                LOGGER.info("countFilesInSubdirectories - " +
                        "STEP-2: Directory [{}] contains [{}] files (exclude subdirectories).", computer.getPath(), computer.getNumberLocalFiles());
            }
        }

        numberSubdirectoryFiles = counter;

        LOGGER.info("countFilesInSubdirectories - " +
                "STEP-2: There are [{}] files in directory [{}] (include subdirectories).", counter, path);

        return Collections.EMPTY_LIST;
    }

    @Compute(step = 3)
    public List countFiles(@Out FileCounterStoreImpl store) {

        long counter;
        counter = numberLocalFiles + numberSubdirectoryFiles;
        numberFiles = counter;

        store.putFileNumber(path, numberFiles);

        LOGGER.info("countFiles - STEP-3: Full quantity files in directory [{}] is [{}]", path, counter);

        return Collections.EMPTY_LIST;
    }

    private long getNumberFiles() {
        return numberFiles;
    }

    public long getNumberLocalFiles() {
        return numberLocalFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OtherFileCounterComputer)) return false;

        OtherFileCounterComputer that = (OtherFileCounterComputer) o;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
