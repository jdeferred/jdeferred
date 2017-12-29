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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.AlwaysPipe;
import org.jdeferred2.CallbackExceptionHandler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see Promise
 * @author Ray Tsang
 *
 */
public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {
	final protected Logger log = LoggerFactory.getLogger(AbstractPromise.class);

	protected volatile State state = State.PENDING;

	protected final List<DoneCallback<? super D>> doneCallbacks = new CopyOnWriteArrayList<DoneCallback<? super D>>();
	protected final List<FailCallback<? super F>> failCallbacks = new CopyOnWriteArrayList<FailCallback<? super F>>();
	protected final List<ProgressCallback<? super P>> progressCallbacks = new CopyOnWriteArrayList<ProgressCallback<? super P>>();
	protected final List<AlwaysCallback<? super D, ? super F>> alwaysCallbacks = new CopyOnWriteArrayList<AlwaysCallback<? super D, ? super F>>();

	protected D resolveResult;
	protected F rejectResult;

	@Override
	public State state() {
		return state;
	}

	@Override
	public Promise<D, F, P> done(DoneCallback<? super D> callback) {
		synchronized (this) {
			if (isResolved()){
				triggerDone(callback, resolveResult);
			}else{
				doneCallbacks.add(callback);
			}
		}
		return this;
	}

	@Override
	public Promise<D, F, P> fail(FailCallback<? super F> callback) {
		synchronized (this) {
			if(isRejected()){
				triggerFail(callback, rejectResult);
			}else{
				failCallbacks.add(callback);
			}
		}
		return this;
	}

	@Override
	public Promise<D, F, P> always(AlwaysCallback<? super D, ? super F> callback) {
		synchronized (this) {
			if(isPending()){
				alwaysCallbacks.add(callback);
			}else{
				triggerAlways(callback, state, resolveResult, rejectResult);
			}
		}
		return this;
	}

	protected void triggerDone(D resolved) {
		for (DoneCallback<? super D> callback : doneCallbacks) {
			triggerDone(callback, resolved);
		}
		doneCallbacks.clear();
	}

	protected void triggerDone(DoneCallback<? super D> callback, D resolved) {
		try {
			callback.onDone(resolved);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.DONE_CALLBACK, e);
		}
	}

	protected void triggerFail(F rejected) {
		for (FailCallback<? super F> callback : failCallbacks) {
			triggerFail(callback, rejected);
		}
		failCallbacks.clear();
	}

	protected void triggerFail(FailCallback<? super F> callback, F rejected) {
		try {
			callback.onFail(rejected);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.FAIL_CALLBACK, e);
		}
	}

	protected void triggerProgress(P progress) {
		for (ProgressCallback<? super P> callback : progressCallbacks) {
			triggerProgress(callback, progress);
		}
	}

	protected void triggerProgress(ProgressCallback<? super P> callback, P progress) {
		try {
			callback.onProgress(progress);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.PROGRESS_CALLBACK, e);
		}
	}

	protected void triggerAlways(State state, D resolve, F reject) {
		for (AlwaysCallback<? super D, ? super F> callback : alwaysCallbacks) {
			triggerAlways(callback, state, resolve, reject);
		}
		alwaysCallbacks.clear();

		synchronized (this) {
			this.notifyAll();
		}
	}

	protected void triggerAlways(AlwaysCallback<? super D, ? super F> callback, State state, D resolve, F reject) {
	    try {
			callback.onAlways(state, resolve, reject);
		} catch (Exception e) {
	        handleException(CallbackExceptionHandler.CallbackType.ALWAYS_CALLBACK, e);
		}
	}

	@Override
	public Promise<D, F, P> progress(ProgressCallback<? super P> callback) {
		progressCallbacks.add(callback);
		return this;
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<? super D> callback) {
		return done(callback);
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback) {
		done(doneCallback);
		fail(failCallback);
		return this;
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<? super D> doneCallback, FailCallback<? super F> failCallback,
			ProgressCallback<? super P> progressCallback) {
		done(doneCallback);
		fail(failCallback);
		progress(progressCallback);
		return this;
	}

	@Override
	public <D_OUT> Promise<D_OUT, F, P> then(
			DoneFilter<? super D, ? extends D_OUT> doneFilter) {
		return new FilteredPromise<D, F, P, D_OUT, F, P>(this, doneFilter, null, null);
	}

	@Override
	public <D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> then(
			DoneFilter<? super D, ? extends D_OUT> doneFilter, FailFilter<? super F, ? extends F_OUT> failFilter) {
		return new FilteredPromise<D, F, P, D_OUT, F_OUT, P>(this, doneFilter, failFilter, null);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<? super D, ? extends D_OUT> doneFilter, FailFilter<? super F, ? extends F_OUT> failFilter,
			ProgressFilter<? super P, ? extends P_OUT> progressFilter) {
		return new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
	}

	@Override
	public <D_OUT> Promise<D_OUT, F, P> then(
			DonePipe<? super D, ? extends D_OUT, ? extends F, ? extends P> doneFilter) {
		return new PipedPromise<D, F, P, D_OUT, F, P>(this, doneFilter, null, null);
	}

	@Override
	public <D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> then(
			DonePipe<? super D, ? extends D_OUT, ? extends F_OUT, ? extends P> doneFilter,
			FailPipe<? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> failFilter) {
		return new PipedPromise<D, F, P, D_OUT, F_OUT, P>(this, doneFilter, failFilter, null);
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<? super D, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> doneFilter,
			FailPipe<? super F, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> failFilter,
			ProgressPipe<? super P, ? extends D_OUT, ? extends F_OUT, ? extends P_OUT> progressFilter) {
		return new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
	}

	@Override
	public <D_OUT, F_OUT> Promise<D_OUT, F_OUT, P> always(AlwaysPipe<? super D, ? super F, ? extends D_OUT, ? extends F_OUT, ? extends P> alwaysFilter) {
		return new PipedPromise<D, F, P, D_OUT, F_OUT, P>(this, alwaysFilter);
	}

	@Override
	public boolean isPending() {
		return state == State.PENDING;
	}

	@Override
	public boolean isResolved() {
		return state == State.RESOLVED;
	}

	@Override
	public boolean isRejected() {
		return state == State.REJECTED;
	}

	public void waitSafely() throws InterruptedException {
		waitSafely(-1);
	}
	public void waitSafely(long timeout) throws InterruptedException {
		final long startTime = System.currentTimeMillis();
		synchronized (this) {
			while (this.isPending()) {
				try {
					if (timeout <= 0) {
						wait();
					} else {
						final long elapsed = (System.currentTimeMillis() - startTime);
					    final long waitTime = timeout - elapsed;
						wait(waitTime);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw e;
				}

				if (timeout > 0 && ((System.currentTimeMillis() - startTime) >= timeout)) {
					return;
				} else {
					continue; // keep looping
				}
			}
		}
	}

	protected void handleException(CallbackExceptionHandler.CallbackType callbackType, Exception e) {
		GlobalConfiguration.getGlobalCallbackExceptionHandler().handleException(callbackType, e);
	}
}
