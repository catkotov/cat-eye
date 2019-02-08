package org.cat.eye.common.util.file;

import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class JarFileUtilTest {

//    public static final String CLASS_PATH =
//            "E:\\Projects\\cat-eye\\cat-eye\\modules\\cat-eye-engine\\cat-eye-engine-container\\target\\test-classes";

    @Test
    public void getClassesNamesFromJarTest() {

        String fullPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        List<String> classLst = ClassFileUtil.getClassesNamesFromJar(fullPath + "cat-eye-test-bungle-simple-0.1-SNAPSHOT-simple.jar");
        assertNotNull(classLst);
    }

    @Test
    public void getClassesNamesFromClassPathTest() {

        String fullPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        List<String> classLst = ClassFileUtil.getClassNamesFromPath(fullPath);
        assertNotNull(classLst);
    }
}