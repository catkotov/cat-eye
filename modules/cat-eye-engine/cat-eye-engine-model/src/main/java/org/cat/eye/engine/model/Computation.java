package org.cat.eye.engine.model;

import java.util.UUID;

/**
 * Created by Kotov on 15.09.2017.
 */
public interface Computation {

    UUID getId();

    String getComputerName();

    String getDomain();
}