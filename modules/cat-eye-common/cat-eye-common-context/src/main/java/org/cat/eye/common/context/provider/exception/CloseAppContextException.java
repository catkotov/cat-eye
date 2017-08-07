package org.cat.eye.common.context.provider.exception;

public class CloseAppContextException extends CatEyeRuntimeException {

    public CloseAppContextException(String message) {
        super(message);
    }

    public CloseAppContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
