/*
 * Copyright 2013-2017 Ray Tsang
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
package org.jdeferred2.impl;

import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.AlwaysPipe;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.DoneFilter;
import org.jdeferred2.DonePipe;
import org.jdeferred2.FailCallback;
import org.jdeferred2.FailFilter;
import org.jdeferred2.FailPipe;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.ProgressFilter;
import org.jdeferred2.ProgressPipe;
import org.jdeferred2.Promise;

/**
 * Promise implementation of the Decorator Pattern.
 *
 * @param <D> Type used for {@link #done(DoneCallback)}
 * @param <F> Type used for {@link #fail(FailCallback)}
 * @param <P> Type used for {@link #progress(ProgressCallback)}
 *
 * @author Andres Almiray
 * @since 1.2.5
 */
public abstract class DelegatingPromise<D, F, P> implements Promise<D, F, P> {
    private final Promise<D, F, P> delegate;

    public DelegatingPromise(Promise<D, F, P> delegate) {
        if (null == delegate) {
            throw new NullPointerException("Argument 'delegate' must not be null");
        }
        this.delegate = delegate;
    }

    /**
     * Returns the delegate Promise wrapped by this Promise.
     *
     * @return
     */
    protected Promise<D, F, P> getDelegate() {
        return delegate;
    }

    @Override
    public State state() {
        return getDelegate().state();
    }

    @Override
    public boolean isPending() {
        return getDelegate().isPending();
    }

    @Override
    public boolean isResolved() {
        return getDelegate().isResolved();
    }

    @Override
    public boolean isRejected() {
        return getDelegate().isRejected();
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
        return getDelegate().then(doneCallback);
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
        return getDelegate().then(doneCallback, failCallback);
    }

    @Override
    public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
        return getDelegate().then(doneCallback, failCallback, progressCallback);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter) {
        return getDelegate().then(doneFilter);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
        return getDelegate().then(doneFilter, failFilter);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter, ProgressFilter<P, P_OUT> progressFilter) {
        return getDelegate().then(doneFilter, failFilter, progressFilter);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
        return getDelegate().then(donePipe);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
        return getDelegate().then(donePipe, failPipe);
    }

    @Override
    public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe, ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {
        return getDelegate().then(donePipe, failPipe, progressPipe);
    }

    @Override
    public <D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> always(AlwaysPipe<D, F, D_OUT, F_OUT, P> alwaysPipe) {
        return getDelegate().always(alwaysPipe);
    }

    @Override
    public Promise<D, F, P> done(DoneCallback<D> callback) {
        return getDelegate().done(callback);
    }

    @Override
    public Promise<D, F, P> fail(FailCallback<F> callback) {
        return getDelegate().fail(callback);
    }

    @Override
    public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
        return getDelegate().always(callback);
    }

    @Override
    public Promise<D, F, P> progress(ProgressCallback<P> callback) {
        return getDelegate().progress(callback);
    }

    @Override
    public void waitSafely() throws InterruptedException {
        getDelegate().waitSafely();
    }

    @Override
    public void waitSafely(long timeout) throws InterruptedException {
        getDelegate().waitSafely(timeout);
    }
}
