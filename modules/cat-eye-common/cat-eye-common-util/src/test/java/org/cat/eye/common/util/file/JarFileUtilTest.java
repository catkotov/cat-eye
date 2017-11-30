package org.cat.eye.common.util.file;

import org.junit.Test;

public class JarFileUtilTest {
    @Test
    public void getClassesNames() throws Exception {

        String fullPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

        ClassFileUtil.getClassesNamesFromJar(fullPath + "cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar");
    }

}