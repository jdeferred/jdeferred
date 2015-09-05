/*******************************************************************************
 * Copyright 2013 Ray Tsang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.jdeferred.android;

import java.lang.reflect.Method;

import org.jdeferred.*;
import org.jdeferred.android.annotation.ExecutionScope;
import org.jdeferred.impl.DeferredObject;

import android.os.Looper;
import android.os.Handler;
import android.os.Message;

public class AndroidDeferredObject<D, F, P> extends DeferredObject<D, F, P> implements AndroidPromise<D, F, P>, AndroidDeferred<D, F, P> {
	private static final InternalHandler sHandler = new InternalHandler();

	private static final int MESSAGE_POST_DONE = 0x1;
	private static final int MESSAGE_POST_PROGRESS = 0x2;
	private static final int MESSAGE_POST_FAIL = 0x3;
	private static final int MESSAGE_POST_ALWAYS = 0x4;

	private final AndroidExecutionScope defaultAndroidExecutionScope;

	public AndroidDeferredObject() {
		this(new DeferredObject<D, F, P>());
	}

	public AndroidDeferredObject(Promise<D, F, P> promise) {
		this(promise, AndroidExecutionScope.UI);
	}

	public AndroidDeferredObject(Promise<D, F, P> promise,
			AndroidExecutionScope defaultAndroidExecutionScope) {
		this.defaultAndroidExecutionScope = defaultAndroidExecutionScope;
		promise.done(new DoneCallback<D>() {
			@Override
			public void onDone(D result) {
				AndroidDeferredObject.this.resolve(result);
			}
		}).progress(new ProgressCallback<P>() {
			@Override
			public void onProgress(P progress) {
				AndroidDeferredObject.this.notify(progress);
			}
		}).fail(new FailCallback<F>() {
			@Override
			public void onFail(F result) {
				AndroidDeferredObject.this.reject(result);
			}
		});
	}

	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter) {
		return new AndroidPipedPromise<>(this, doneFilter, null, null);
	}

	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
			AndroidFailPipe<F, D_OUT, F_OUT, P_OUT> failFilter) {
		return new AndroidPipedPromise<>(this, doneFilter, failFilter, null);
	}

	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
			AndroidFailPipe<F, D_OUT, F_OUT, P_OUT> failFilter,
			AndroidProgressPipe<P, D_OUT, F_OUT, P_OUT> progressFilter) {
		return new AndroidPipedPromise<>(this, doneFilter, failFilter, progressFilter);
	}


	public AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback) {
		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback));
	}

	public AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback,
										AndroidFailCallback<F> failCallback) {
		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback, failCallback));
	}

	public AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback,
										AndroidFailCallback<F> failCallback, AndroidProgressCallback<P> progressCallback) {
		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback, failCallback, progressCallback));
	}

	@Override
	public AndroidPromise<D, F, P> then(DoneCallback<D> doneCallback) {
		assertAndroidContext(doneCallback, AndroidDoneCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback));
	}

	@Override
	public AndroidPromise<D, F, P> then(DoneCallback<D> doneCallback,
								 FailCallback<F> failCallback) {
		assertAndroidContext(doneCallback, AndroidDoneCallback.class);
		assertAndroidContext(failCallback, AndroidFailCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback, failCallback));
	}

	@Override
	public AndroidPromise<D, F, P> then(DoneCallback<D> doneCallback,
								 FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
		assertAndroidContext(doneCallback, AndroidDoneCallback.class);
		assertAndroidContext(failCallback, AndroidFailCallback.class);
		assertAndroidContext(progressCallback, AndroidProgressCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(doneCallback, failCallback, progressCallback));
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter,
			ProgressFilter<P, P_OUT> progressFilter) {
		assertAndroidContext(doneFilter, AndroidDoneFilter.class);
		assertAndroidContext(failFilter, AndroidFailFilter.class);
		assertAndroidContext(failFilter, AndroidProgressFilter.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(doneFilter, failFilter, progressFilter));
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe) {
		assertAndroidContext(donePipe, AndroidDonePipe.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(donePipe));
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe) {
		assertAndroidContext(donePipe, AndroidDonePipe.class);
		assertAndroidContext(failPipe, AndroidFailPipe.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(donePipe, failPipe));
	}

	@Override
	public <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
			DonePipe<D, D_OUT, F_OUT, P_OUT> donePipe, FailPipe<F, D_OUT, F_OUT, P_OUT> failPipe,
			ProgressPipe<P, D_OUT, F_OUT, P_OUT> progressPipe) {

		assertAndroidContext(donePipe, AndroidDonePipe.class);
		assertAndroidContext(failPipe, AndroidFailPipe.class);
		assertAndroidContext(progressPipe, AndroidProgressPipe.class);

		return AndroidDeferredManager.asAndroidPromise(super.then(donePipe, failPipe, progressPipe));
	}

	@Override
	public AndroidPromise<D, F, P> done(DoneCallback<D> callback) {
		assertAndroidContext(callback, AndroidDoneCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.done(callback));
	}

	@Override
	public AndroidPromise<D, F, P> fail(FailCallback<F> callback) {
		assertAndroidContext(callback, AndroidFailCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.fail(callback));
	}

	@Override
	public AndroidPromise<D, F, P> always(AlwaysCallback<D, F> callback) {
		assertAndroidContext(callback, AndroidAlwaysCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.always(callback));
	}

	@Override
	public AndroidPromise<D, F, P> progress(ProgressCallback<P> callback) {
		assertAndroidContext(callback, AndroidProgressCallback.class);

		return AndroidDeferredManager.asAndroidPromise(super.progress(callback));
	}

	// These are never called, because the parameters are subclasses of methods that are already defined in Promise-interface

	@Override
	public AndroidPromise<D, F, P> done(AndroidDoneCallback<D> callback) {
		return AndroidDeferredManager.asAndroidPromise(super.done(callback));
	}

	@Override
	public AndroidPromise<D, F, P> fail(AndroidFailCallback<F> callback) {
		return AndroidDeferredManager.asAndroidPromise(super.fail(callback));
	}

	@Override
	public AndroidPromise<D, F, P> always(AndroidAlwaysCallback<D, F> callback) {
		return AndroidDeferredManager.asAndroidPromise(super.always(callback));
	}

	//


	@Override
	public AndroidDeferred<D, F, P> resolve(final D resolve) {
		return AndroidDeferredManager.asAndroidDeferred(super.resolve(resolve));
	}

	@Override
	public AndroidDeferred<D, F, P> notify(final P progress) {
		return AndroidDeferredManager.asAndroidDeferred(super.notify(progress));
	}

	@Override
	public AndroidDeferred<D, F, P> reject(final F reject) {
		return AndroidDeferredManager.asAndroidDeferred(super.reject(reject));
	}

	@Override
	public AndroidPromise<D, F, P> promise() {
		return AndroidDeferredManager.asAndroidPromise(super.promise());
	}

	private static class InternalHandler extends Handler {
		public InternalHandler() {
			super(Looper.getMainLooper());
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void handleMessage(Message msg) {
			CallbackMessage<?, ?, ?, ?> result = (CallbackMessage<?, ?, ?, ?>) msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_DONE:
				((DoneCallback) result.callback).onDone(result.resolved);
				break;
			case MESSAGE_POST_PROGRESS:
				((ProgressCallback) result.callback)
						.onProgress(result.progress);
				break;
			case MESSAGE_POST_FAIL:
				((FailCallback) result.callback).onFail(result.rejected);
				break;
			case MESSAGE_POST_ALWAYS:
				((AlwaysCallback) result.callback).onAlways(result.state,
						result.resolved, result.rejected);
				break;
			}
		}
	}

	private void assertAndroidContext(Object o, Class type) {
		if (!type.isInstance(o)) {
			throw new Error("Trying to access non-Android method in Android context");
		}
	}

	protected void triggerDone(DoneCallback<D> callback, D resolved) {
		if (determineAndroidExecutionScope(callback) == AndroidExecutionScope.UI) {
			executeInUiThread(MESSAGE_POST_DONE, callback, State.RESOLVED,
					resolved, null, null);
		} else {
			super.triggerDone(callback, resolved);
		}
	};

	protected void triggerFail(FailCallback<F> callback, F rejected) {
		if (determineAndroidExecutionScope(callback) == AndroidExecutionScope.UI) {
			executeInUiThread(MESSAGE_POST_FAIL, callback, State.REJECTED,
					null, rejected, null);
		} else {
			super.triggerFail(callback, rejected);
		}
	};

	protected void triggerProgress(ProgressCallback<P> callback, P progress) {
		if (determineAndroidExecutionScope(callback) == AndroidExecutionScope.UI) {
			executeInUiThread(MESSAGE_POST_PROGRESS, callback, State.PENDING,
					null, null, progress);
		} else {
			super.triggerProgress(callback, progress);
		}
	};

	protected void triggerAlways(AlwaysCallback<D, F> callback, State state,
			D resolve, F reject) {
		if (determineAndroidExecutionScope(callback) == AndroidExecutionScope.UI) {
			executeInUiThread(MESSAGE_POST_ALWAYS, callback, state, resolve,
					reject, null);
		} else {
			super.triggerAlways(callback, state, resolve, reject);
		}
	};

	protected <Callback> void executeInUiThread(int what, Callback callback,
			State state, D resolve, F reject, P progress) {
		Message message = sHandler.obtainMessage(what,
				new CallbackMessage<>(this, callback, state,
						resolve, reject, progress));
		message.sendToTarget();
	}

	protected AndroidExecutionScope determineAndroidExecutionScope(Class<?> clazz, String methodName, Class<?> ... arguments) {
		ExecutionScope scope = null;

		if (methodName != null) {
			try {
				Method method = clazz.getMethod(methodName, arguments);
				scope = method.getAnnotation(ExecutionScope.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
		if (scope == null) {
			scope = clazz.getAnnotation(ExecutionScope.class);
		}

		return scope == null ? defaultAndroidExecutionScope : scope.value();
	}

	protected AndroidExecutionScope determineAndroidExecutionScope(Object callback) {
		AndroidExecutionScope scope = null;
		if (callback instanceof AndroidExecutionScopeable) {
			scope = ((AndroidExecutionScopeable) callback).getExecutionScope();
		} else if (callback instanceof DoneCallback) {
			return determineAndroidExecutionScope(callback.getClass(), "onDone", Object.class);
		} else if (callback instanceof FailCallback) {
			return determineAndroidExecutionScope(callback.getClass(), "onFail", Object.class);
		} else if (callback instanceof ProgressCallback) {
			return determineAndroidExecutionScope(callback.getClass(), "onProgress", Object.class);
		} else if (callback instanceof DonePipe) {
			return determineAndroidExecutionScope(callback.getClass(), "pipeDone", Object.class);
		} else if (callback instanceof AlwaysCallback) {
			return determineAndroidExecutionScope(callback.getClass(), "onAlways", State.class, Object.class, Object.class);
		}
		return scope == null ? defaultAndroidExecutionScope : scope;
	}

	@SuppressWarnings("rawtypes")
	private static class CallbackMessage<Callback, D, F, P> {
		final Deferred deferred;
		final Callback callback;
		final D resolved;
		final F rejected;
		final P progress;
		final State state;

		CallbackMessage(Deferred deferred, Callback callback, State state,
				D resolved, F rejected, P progress) {
			this.deferred = deferred;
			this.callback = callback;
			this.state = state;
			this.resolved = resolved;
			this.rejected = rejected;
			this.progress = progress;
		}
	}

}
