package org.cat.eye.engine.container.deployment.management;

public interface BundleManager {

    Bundle getBundle(String domain);

    void putBundle(Bundle bundle);

}
