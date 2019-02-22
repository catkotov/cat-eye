package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.*;
import org.cat.eye.test.bundle.model.impl.FileCounterStoreImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kotov on 30.09.2017.
 */
@IsComputable
public class FileCounterComputer implements Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileCounterComputer.class);

    @Id
    private final String path;

    private List<FileCounterComputer> computers = new ArrayList<>();

    private int numberLocalFiles;


    public FileCounterComputer(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Compute
    public List<FileCounterComputer> getDirectoriesAndFileCounter(@In FileCounterStoreImpl store) {

        int fileCount = 0;

        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        computers.add(new FileCounterComputer(file.getAbsolutePath()));
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

        if (fileCount != 0) {
            store.putFileNumber(path, fileCount);
        }

        return computers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileCounterComputer)) return false;

        FileCounterComputer that = (FileCounterComputer) o;

        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
