package org.cat.eye.engine.container.deployment.management;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kotov on 29.10.2017.
 */
public class BundleManagerImpl implements BundleManager {

    private Map<String, Bundle> bundles = new ConcurrentHashMap<>();

    @Override
    public Bundle getBundle(String domain) {
        return bundles.get(domain);
    }

    @Override
    public void putBundle(Bundle bundle) {
        bundles.put(bundle.getDomain(), bundle);
    }
}
