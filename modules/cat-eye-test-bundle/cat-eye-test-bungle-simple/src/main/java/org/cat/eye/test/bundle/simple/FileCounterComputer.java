package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.Compute;
import org.cat.eye.engine.model.annotation.Id;
import org.cat.eye.engine.model.annotation.IsComputable;
import org.cat.eye.engine.model.annotation.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kotov on 30.09.2017.
 */
@IsComputable
public class FileCounterComputer {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileCounterComputer.class);

    @Id
    private final String path;

    private int numberLocalFiles;

    private long numberSubdirectoryFiles;

    private long numberFiles;

    private List<FileCounterComputer> computers = new ArrayList<>();

    public FileCounterComputer(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Compute(step = 1)
    public List<FileCounterComputer> getDirectoriesAndFileCounte() {

        int fileCount = 0;

        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        computers.add(new FileCounterComputer(file.getAbsolutePath()));
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
    @Update(fieldNames = {"computers", "numberSubdirectoryFiles"})
    public List countFilesInSubdirectories() {

        long counter = 0;

        if (!this.computers.isEmpty()) {
            for (FileCounterComputer computer : computers) {
                counter =+ computer.getNumberLocalFiles();
            }
        }

        numberSubdirectoryFiles = counter;

        return Collections.EMPTY_LIST;
    }

    @Compute(step = 3)
    public List countFiles() {

        long counter = 0;
        counter = numberLocalFiles + numberSubdirectoryFiles;
        numberFiles = counter;

        return Collections.EMPTY_LIST;
    }

    public int getNumberLocalFiles() {
        return numberLocalFiles;
    }

    public long getNumberFiles() {
        return numberFiles;
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
