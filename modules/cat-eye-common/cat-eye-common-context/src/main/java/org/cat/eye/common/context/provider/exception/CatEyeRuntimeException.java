package org.cat.eye.common.context.provider.exception;

/**
 *
 */
public class CatEyeRuntimeException extends RuntimeException {

    public CatEyeRuntimeException(String message) {
        super(message);
    }

    public CatEyeRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
