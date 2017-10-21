package org.cat.eye.common.util.file;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileUtil {

    public static List<String> getClassesNames(String jarFilePath) {

        File file = new File(jarFilePath);

        if (file.exists() && file.getName().endsWith(".jar")) {
            try {
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> jarEntities = jarFile.entries();
                if (jarEntities != null) {

                }

            } catch (IOException e) {

            }
        } else {

        }

        return null;
    }
}
