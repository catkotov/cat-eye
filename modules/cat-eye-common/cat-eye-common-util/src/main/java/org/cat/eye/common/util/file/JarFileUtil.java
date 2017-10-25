package org.cat.eye.common.util.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(JarFileUtil.class);

    private final static String FILE_SEPARATOR = "/";

    public static List<String> getClassesNames(String jarFilePath) {

        List<String> result = new ArrayList<>();

        File file = new File(jarFilePath);

        if (file.exists() && file.getName().endsWith(".jar")) {
            try {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> jarEntities = jarFile.entries();
                if (jarEntities != null) {
                    while (jarEntities.hasMoreElements()) {
                        JarEntry jarEntry = jarEntities.nextElement();
                        if (jarEntry.getName().endsWith(".class")) {
                            result.add(parseJarEntryName(jarEntry.getName()));
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("JarFileUtil.getClassesNames - can't create list of classes names.", e);
            }
        } else {
            LOGGER.warn("JarFileUtil.getClassesNames - the file " + jarFilePath + " isn't jar file!");
        }

        return result;
    }

    private static String parseJarEntryName(String jarEntryName) {
        return jarEntryName.replace(FILE_SEPARATOR, ".").substring(0, jarEntryName.lastIndexOf('.'));
    }
}
