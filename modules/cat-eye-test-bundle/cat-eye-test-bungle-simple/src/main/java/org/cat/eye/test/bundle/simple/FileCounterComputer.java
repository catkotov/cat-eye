package org.cat.eye.test.bundle.simple;

import org.cat.eye.engine.model.annotation.Compute;
import org.cat.eye.engine.model.annotation.IsComputable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kotov on 30.09.2017.
 */
@IsComputable
public class FileCounterComputer {

    @Compute
    public List<String> getDirectoriesAndFileCounte(String directoryPath) {

        List<String> direcotries = new ArrayList<String>();

        int fileCount = 0;

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    direcotries.add(file.getAbsolutePath());
                } else {
                    fileCount++;
                }
            }
        }

        // write result

        return direcotries;
    }
}
