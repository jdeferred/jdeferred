package org.jdeferred;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nwertzberger on 1/12/16.
 */
public class PromisesTest {

    @Test
    public void rejected_isRejected() throws Exception {
        assertTrue(Promises.rejected("this").isRejected());
    }

    @Test
    public void resolved_isResolved() throws Exception {
        assertTrue(Promises.resolved("this").isResolved());
    }

    @Test
    public void progress_isProgressed() throws Exception {
        Promises.progressed("progress").progress(
            new ProgressCallback<String>() {
                @Override
                public void onProgress(String progress) {
                    assertEquals("progress", progress);
                }
            }
        ).waitSafely(1);

    }

}