package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.*;
import org.cat.eye.test.bundle.model.FileCounterStore;
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

    public OtherFileCounterComputer() {
        this.path = "C:/";
    }

    public OtherFileCounterComputer(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Compute(step = 1)
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
    public List countFilesInSubdirectories(@In FileCounterStore store) {

        long counter = 0;

        if (!this.computers.isEmpty()) {
            for (OtherFileCounterComputer computer : computers) {
                counter =+ store.getFileNumber(computer.getPath());

                LOGGER.info("countFilesInSubdirectories - " +
                        "Directory [{0}] contains [{1}] files.", computer.getPath(), computer.getNumberFiles());
            }
        }

        numberSubdirectoryFiles = counter;

        LOGGER.info("countFilesInSubdirectories - " +
                "There are [{}] in subdirectories of directory [{}].", counter, path);

        return Collections.EMPTY_LIST;
    }

    @Compute(step = 3)
    public List countFiles(@Out FileCounterStore store) {

        long counter = 0;
        counter = numberLocalFiles + numberSubdirectoryFiles;
        numberFiles = counter;

        store.putFileNumber(path, numberFiles);

        LOGGER.info("countFiles - Full quantity files in directory [{0}] is [{1}]", path, counter);

        return Collections.EMPTY_LIST;
    }

    public long getNumberFiles() {
        return numberFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileCounterComputer)) return false;

        OtherFileCounterComputer that = (OtherFileCounterComputer) o;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
