package org.jdeferred;

import org.jdeferred.impl.DefaultExceptionHandler;

public final class GlobalDeferredManagerConfiguration {
    private static ExceptionHandler globalExceptionHandler = new DefaultExceptionHandler();

    private GlobalDeferredManagerConfiguration() {};

    public static void setGlobalExceptionHandler(ExceptionHandler exceptionHandler) {
        globalExceptionHandler = exceptionHandler;
    }

    public static ExceptionHandler getGlobalExceptionHandler() {
        return globalExceptionHandler;
    }
}
