package org.jdeferred.impl;

import org.jdeferred.ExceptionHandler;
import org.jdeferred.impl.DefaultExceptionHandler;

public final class GlobalConfiguration {
    private static ExceptionHandler globalExceptionHandler = new DefaultExceptionHandler();

    private GlobalConfiguration() {};

    public static void setGlobalExceptionHandler(ExceptionHandler exceptionHandler) {
        if (exceptionHandler == null) {
            throw new IllegalArgumentException("exceptionHandler cannot be null");
        }
        globalExceptionHandler = exceptionHandler;
    }

    public static ExceptionHandler getGlobalExceptionHandler() {
        return globalExceptionHandler;
    }
}
