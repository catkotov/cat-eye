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
public class OtherFileCounterComputer implements OtherFileCounter {

    private final static Logger LOGGER = LoggerFactory.getLogger(OtherFileCounterComputer.class);

    @Id
    private final String path;

    private List<OtherFileCounterComputer> computers = new ArrayList<>();

    private long numberSubdirectoryFiles;

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
                                "Computation with path [{}} was created.", file.getAbsolutePath());
                    } else {
                        fileCount++;
                    }
                }
            }
        }

        // write result
        numberLocalFiles = fileCount;

        return computers;
    }

    @Compute(step = 2)
    public List countFilesInSubdirectories(@In FileCounterStoreImpl store) {

        long counter = 0;

        if (!this.computers.isEmpty()) {
            for (OtherFileCounterComputer computer : computers) {
                counter =+ store.getFileNumber(computer.getPath());

                LOGGER.info("countFilesInSubdirectories - " +
                        "Directory [{}] contains [{}] files.", computer.getPath(), computer.getNumberFiles());
            }
        }

        numberSubdirectoryFiles = counter;

        LOGGER.info("countFilesInSubdirectories - " +
                "There are [{}] subdirectories in directory [{}].", counter, path);

        return Collections.EMPTY_LIST;
    }

    @Compute(step = 3)
    public List countFiles(@Out FileCounterStoreImpl store) {

        long counter;
        counter = numberLocalFiles + numberSubdirectoryFiles;
        numberFiles = counter;

        store.putFileNumber(path, numberFiles);

        LOGGER.info("countFiles - Full quantity files in directory [{}] is [{}]", path, counter);

        return Collections.EMPTY_LIST;
    }

    private long getNumberFiles() {
        return numberFiles;
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
