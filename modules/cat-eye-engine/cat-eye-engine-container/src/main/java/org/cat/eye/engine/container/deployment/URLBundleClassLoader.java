package org.cat.eye.engine.container.deployment;

import sun.misc.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by Kotov on 20.10.2017.
 */
public class URLBundleClassLoader extends URLClassLoader {

    public URLBundleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

//    @Override
//    protected Class<?> findClass(final String name)
//            throws ClassNotFoundException {
//
//        return super.findClass(name);
//    }

}
