package org.cat.eye.engine.common;

import java.util.Arrays;

public enum ContainerRole {

    DRIVER("driver"),
    DISPATCHER("dispatcher"),
    ENGINE("engine");

    String role;

    ContainerRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static ContainerRole defineRole(String roleName) {

        return Arrays.stream(ContainerRole.values())
                .filter((role) -> role.getRole().equals(roleName)).findFirst()
                .orElse(null);

    }
}
