package org.cat.eye.engine.common.deployment.management;

import java.util.Collection;

public interface BundleManager {

    Bundle getBundle(String domain);

    void putBundle(Bundle bundle);

    Collection<Bundle> getBundles();

}
