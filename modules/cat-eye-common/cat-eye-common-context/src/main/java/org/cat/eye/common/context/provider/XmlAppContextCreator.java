package org.cat.eye.common.context.provider;

import org.springframework.core.io.Resource;
import java.io.IOException;

public interface XmlAppContextCreator extends AppContextCreator {

    void addConfigLocation(String configClass) throws IOException;

    void addConfigLocation(Resource resource) throws IOException;

}
