package org.cat.eye.engine.common.deployment;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by Kotov on 20.10.2017.
 */
public class URLBundleClassLoader extends URLClassLoader {

    public URLBundleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

}
