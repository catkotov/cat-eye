package org.cat.eye.engine.common.deployment.management;

public interface BundleManager {

    Bundle getBundle(String domain);

    void putBundle(Bundle bundle);

}
