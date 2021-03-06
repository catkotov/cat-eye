package org.cat.eye.engine.common.deployment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kotov on 12.10.2017.
 */
public class BundleClassLoader extends ClassLoader {

    private final static Logger LOGGER = LoggerFactory.getLogger(BundleClassLoader.class);

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        LOGGER.info("BundleClassLoader.loadClass - loading class <" + name + ">");

        Class<?> clazz = getClass(name);

        return clazz != null ? clazz : super.loadClass(name);
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {

        String file = name.replace('.', File.separatorChar) + ".class";
        byte[] b;
        try {
            // This loads the byte code data from the file
            b = loadClassData(file);
            if (b != null) {
                // defineClass is inherited from the ClassLoader class
                // and converts the byte array into a Class
                Class<?> c = defineClass(name, b, 0, b.length);
                resolveClass(c);
                return c;
            } else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("BundleClassLoader.getClass - can't get class <" + name + ">", e);
            return null;
        }
    }

    private byte[] loadClassData(String name) throws IOException {
        // Opening the file
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        if (stream != null) {
            int size = stream.available();
            byte buff[] = new byte[size];
            DataInputStream in = new DataInputStream(stream);
            // Reading the binary data
            in.readFully(buff);
            in.close();
            stream.close();

            return buff;
        } else {
            return null;
        }
    }
}
