package org.cat.eye.common.util.file;

import org.junit.Test;

import static org.junit.Assert.*;

public class JarFileUtilTest {
    @Test
    public void getClassesNames() throws Exception {

        String fullPath = Thread.currentThread().getContextClassLoader().getResource("").toString();

        JarFileUtil.getClassesNames(fullPath + "cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar");
    }

}