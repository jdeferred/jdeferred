package org.jdeferred.impl;

import org.jdeferred.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by nwertzberger on 3/17/15.
 */
public class ExceptionsTest {

    public static final String THIS_IS_MY_RUNTIME_EXCEPTION = "This is my runtime exception";

    @Test
    public void testRuntimeExceptionsAreThrownOnWait() throws InterruptedException {
        final DeferredObject<String, Void, Void> deferredObject =
            new DeferredObject<String, Void, Void>();
        Promise<String, Void, Void> promise = deferredObject.promise()
            .done(new DoneCallback<String>() {
                @Override
                public void onDone(String result) {
                    throw new RuntimeException(THIS_IS_MY_RUNTIME_EXCEPTION);
                }
            });
        deferredObject.resolve("test");

        try {
            promise.waitSafely();
            fail();
        } catch (RuntimeException re) {
            assertEquals(THIS_IS_MY_RUNTIME_EXCEPTION, re.getMessage());
        }
    }

    @Test
    public void testDoneExceptionsAreCaught() throws InterruptedException {
        final DeferredObject<String, Void, Void> deferredObject =
            new DeferredObject<String, Void, Void>();
        Promise<String, Void, Void> promise = deferredObject.promise()
            .done(new DoneCallback<String>() {
                @Override
                public void onDone(String result) {
                    throw new RuntimeException(THIS_IS_MY_RUNTIME_EXCEPTION);
                }
            }).except(new ExceptCallback() {
                @Override
                public void onException(Exception e) {
                    assertEquals(THIS_IS_MY_RUNTIME_EXCEPTION, e.getMessage());
                }
            });
        deferredObject.resolve("test");
        try {
            promise.waitSafely();
            fail();
        } catch (RuntimeException e) {

        }
    }

    @Test
    public void testFailureExceptionsAreCaught() throws InterruptedException {
        final DeferredObject<String, Void, Void> deferredObject =
            new DeferredObject<String, Void, Void>();
        Promise<String, Void, Void> promise = deferredObject.promise()
            .except(new ExceptCallback() {
                @Override
                public void onException(Exception e) {
                    assertEquals(THIS_IS_MY_RUNTIME_EXCEPTION, e.getMessage());
                }
            })
            .fail(new FailCallback<Void>() {
                @Override
                public void onFail(Void result) {
                    throw new RuntimeException(THIS_IS_MY_RUNTIME_EXCEPTION);
                }
            });
        deferredObject.reject(null);

        try {
            promise.waitSafely();
            fail();
        } catch (RuntimeException re) {
        }
    }

    @Test
    public void testAlwaysExceptionsAreCaught() throws InterruptedException {
        final DeferredObject<String, Void, Void> deferredObject =
            new DeferredObject<String, Void, Void>();
        Promise<String, Void, Void> promise = deferredObject.promise()
            .except(new ExceptCallback() {
                @Override
                public void onException(Exception e) {
                    assertEquals(THIS_IS_MY_RUNTIME_EXCEPTION, e.getMessage());
                }
            })
            .always(new AlwaysCallback<String, Void>() {
                @Override
                public void onAlways(
                    Promise.State state, String resolved, Void rejected
                ) {
                    throw new RuntimeException(THIS_IS_MY_RUNTIME_EXCEPTION);
                }
            });

        deferredObject.reject(null);

        try {
            promise.waitSafely();
            fail();
        } catch (RuntimeException re) {
        }
    }

    @Test
    public void testProgressExceptionsAreCaught() throws InterruptedException {
        final DeferredObject<String, Void, Void> deferredObject =
            new DeferredObject<String, Void, Void>();
        Promise<String, Void, Void> promise = deferredObject.promise()
            .except(new ExceptCallback() {
                @Override
                public void onException(Exception e) {
                    assertEquals(THIS_IS_MY_RUNTIME_EXCEPTION, e.getMessage());
                }
            })
            .progress(new ProgressCallback<Void>() {
                @Override
                public void onProgress(Void progress) {
                    throw new RuntimeException(THIS_IS_MY_RUNTIME_EXCEPTION);
                }
            });
        deferredObject.notify(null);
        deferredObject.reject(null);

        try {
            promise.waitSafely();
            fail();
        } catch (RuntimeException re) {
        }
    }

}
