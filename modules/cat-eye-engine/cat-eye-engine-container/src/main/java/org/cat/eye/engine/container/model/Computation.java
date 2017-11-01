package org.cat.eye.engine.container.model;

import java.util.UUID;

public interface Computation {

    UUID getId();

    String getName();

    String getDomain();
}
