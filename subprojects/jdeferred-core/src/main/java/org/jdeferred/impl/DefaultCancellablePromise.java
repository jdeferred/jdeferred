/*
 * Copyright Ray Tsang ${author}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.impl;

import org.jdeferred.CancellablePromise;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation of the {@code CancellablePromise} interface.
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> Type used for {@link #progress(ProgressCallback)}
 *
 * @author Andres Almiray
 * @since 1.2.5
 */
public class DefaultCancellablePromise<D, F, P> extends DelegatingPromise<D, F, P> implements CancellablePromise<D, F, P> {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final DeferredFutureTask<D, P> futureTask;

    public DefaultCancellablePromise(Promise<D, F, P> delegate, DeferredFutureTask<D, P> futureTask) {
        super(delegate);
        if (null == futureTask) {
            throw new NullPointerException("Argument 'futureTask' must not be null");
        }
        this.futureTask = futureTask;
    }

    @Override
    public void cancel() {
        try {
            futureTask.cancel(true);
            cancelled.set(true);
        } catch (CancellationException expected) {
            // OK
        }
    }

    public boolean isCancelled() {
        return cancelled.get();
    }
}
