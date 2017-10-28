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
package org.jdeferred.impl;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.CallbackExceptionHandler;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ray Tsang
 * @see Promise
 */
public abstract class AbstractPromise<D, F, P> implements Promise<D, F, P> {
	protected volatile State state = State.PENDING;

	protected final List<DoneCallback<D>> doneCallbacks = new CopyOnWriteArrayList<DoneCallback<D>>();
	protected final List<FailCallback<F>> failCallbacks = new CopyOnWriteArrayList<FailCallback<F>>();
	protected final List<ProgressCallback<P>> progressCallbacks = new CopyOnWriteArrayList<ProgressCallback<P>>();
	protected final List<AlwaysCallback<D, F>> alwaysCallbacks = new CopyOnWriteArrayList<AlwaysCallback<D, F>>();

	protected D resolveResult;
	protected F rejectResult;
	protected CallbackExceptionHandler callbackExceptionHandler;

	@Override
	public State state() {
		return state;
	}

	@Override
	public Promise<D, F, P> done(DoneCallback<D> callback) {
		synchronized (this) {
			if (isResolved()) {
				triggerDone(callback, resolveResult);
			} else {
				doneCallbacks.add(callback);
			}
		}
		return this;
	}

	@Override
	public Promise<D, F, P> fail(FailCallback<F> callback) {
		synchronized (this) {
			if (isRejected()) {
				triggerFail(callback, rejectResult);
			} else {
				failCallbacks.add(callback);
			}
		}
		return this;
	}

	@Override
	public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
		synchronized (this) {
			if (isPending()) {
				alwaysCallbacks.add(callback);
			} else {
				triggerAlways(callback, state, resolveResult, rejectResult);
			}
		}
		return this;
	}

	protected void triggerDone(D resolved) {
		for (DoneCallback<D> callback : doneCallbacks) {
			triggerDone(callback, resolved);
		}
		doneCallbacks.clear();
	}

	protected void triggerDone(DoneCallback<D> callback, D resolved) {
		try {
			callback.onDone(resolved);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.DONE_CALLBACK, e);
		}
	}

	protected void triggerFail(F rejected) {
		for (FailCallback<F> callback : failCallbacks) {
			triggerFail(callback, rejected);
		}
		failCallbacks.clear();
	}

	protected void triggerFail(FailCallback<F> callback, F rejected) {
		try {
			callback.onFail(rejected);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.FAIL_CALLBACK, e);
		}
	}

	protected void triggerProgress(P progress) {
		for (ProgressCallback<P> callback : progressCallbacks) {
			triggerProgress(callback, progress);
		}
	}

	protected void triggerProgress(ProgressCallback<P> callback, P progress) {
		try {
			callback.onProgress(progress);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.PROGRESS_CALLBACK, e);
		}
	}

	protected void triggerAlways(State state, D resolve, F reject) {
		for (AlwaysCallback<D, F> callback : alwaysCallbacks) {
			triggerAlways(callback, state, resolve, reject);
		}
		alwaysCallbacks.clear();

		synchronized (this) {
			this.notifyAll();
		}
	}

	protected void triggerAlways(AlwaysCallback<D, F> callback, State state, D resolve, F reject) {
		try {
			callback.onAlways(state, resolve, reject);
		} catch (Exception e) {
			handleException(CallbackExceptionHandler.CallbackType.ALWAYS_CALLBACK, e);
		}
	}

	@Override
	public Promise<D, F, P> progress(ProgressCallback<P> callback) {
		progressCallbacks.add(callback);
		return this;
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<D> callback) {
		return done(callback);
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback) {
		done(doneCallback);
		fail(failCallback);
		return this;
	}

	@Override
	public Promise<D, F, P> then(DoneCallback<D> doneCallback, FailCallback<F> failCallback,
	                             ProgressCallback<P> progressCallback) {
		done(doneCallback);
		fail(failCallback);
		progress(progressCallback);
		return this;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DoneFilter<D, D_OUT> doneFilter) {
		FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> filteredPromise = new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, null, null);
		filteredPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return filteredPromise;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
		FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> filteredPromise = new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, null);
		filteredPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return filteredPromise;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter,
		ProgressFilter<P, P_OUT> progressFilter) {
		FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> filteredPromise = new FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
		filteredPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return filteredPromise;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter) {
		PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT> pipedPromise = new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, null, null);
		pipedPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return pipedPromise;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
		FailPipe<F, D_OUT, F_OUT, P_OUT> failFilter) {
		PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT> pipedPromise = new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, null);
		pipedPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return pipedPromise;
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
		DonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
		FailPipe<F, D_OUT, F_OUT, P_OUT> failFilter,
		ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressFilter) {
		PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT> pipedPromise = new PipedPromise<D, F, P, D_OUT, F_OUT, P_OUT>(this, doneFilter, failFilter, progressFilter);
		pipedPromise.setCallbackExceptionHandler(callbackExceptionHandler);
		return pipedPromise;
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

	@Override
	public CallbackExceptionHandler getCallbackExceptionHandler() {
		return callbackExceptionHandler;
	}

	public Promise<D, F, P> setCallbackExceptionHandler(CallbackExceptionHandler callbackExceptionHandler) {
		this.callbackExceptionHandler = callbackExceptionHandler;
		return this;
	}

	protected void handleException(CallbackExceptionHandler.CallbackType callbackType, Exception e) {
		if (callbackExceptionHandler != null) {
			callbackExceptionHandler.handleException(callbackType, e);
		} else {
			GlobalConfiguration.getGlobalCallbackExceptionHandler().handleException(callbackType, e);
		}
	}
}
