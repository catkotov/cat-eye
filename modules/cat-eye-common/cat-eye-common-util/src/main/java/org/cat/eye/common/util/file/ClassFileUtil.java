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

public class ClassFileUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClassFileUtil.class);

    private final static String FILE_SEPARATOR = "/";

    public static List<String> getClassesNamesFromJar(String jarFilePath) {

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
                            result.add(parseFullClassName(jarEntry.getName()));
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("JarFileUtil.getClassesNamesFromJar - can't create list of classes names.", e);
            }
        } else {
            LOGGER.warn("JarFileUtil.getClassesNamesFromJar - the file " + jarFilePath + " isn't jar file!");
        }

        return result;
    }

    public static List<String> getClassNamesFromPath(String classPath) {

        List<String> result = new ArrayList<>();

        File file = new File(classPath);

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length != 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        result = traverseDirectoryTree(result, f.getName(), f);
                    } else if (f.getName().endsWith(".class")) {
                        result.add(parseFullClassName(f.getName()));
                    }
                }
            }
        }

        return result;
    }

    private static List<String> traverseDirectoryTree(List<String> classLst, String directoryPath, File file) {

        File[] files = file.listFiles();

        if (files != null && files.length != 0) {
            for (File f : files) {
                if (f.isDirectory()) {
                    traverseDirectoryTree(classLst, directoryPath + "/" + f.getName(), f);
                } else if (f.getName().endsWith(".class")) {
                    classLst.add(parseFullClassName(directoryPath + "/" + f.getName()));
                }
            }
        }

        return classLst;
    }

    private static String parseFullClassName(String jarEntryName) {
        return jarEntryName.replace(FILE_SEPARATOR, ".").substring(0, jarEntryName.lastIndexOf('.'));
    }
}
