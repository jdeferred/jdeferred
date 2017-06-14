/*
 * Copyright 2013-2016 Ray Tsang
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
package org.jdeferred.android;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.MultipleResults2;
import org.jdeferred.multiple.MultipleResults3;
import org.jdeferred.multiple.MultipleResults4;
import org.jdeferred.multiple.MultipleResults5;
import org.jdeferred.multiple.MultipleResultsN;
import org.jdeferred.multiple.OneReject;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * This DeferredManager is designed to execute deferred tasks in the background,
 * but also executes callbacks (e.g., done, fail, progress, and always) in the UI thread.
 * This is important because only UI thread executions can update UI elements!
 * <p>
 * You can use {@link DeferredAsyncTask} to write in the more familiar Android {@link AsyncTask} API
 * and still being able to take advantage of {@link Promise} chaining.
 * <p>
 * Even more powerful, you can also use {@link Promise}, {@link Runnable}, {@link Callable},
 * and any other types supported by {@link DeferredManager}.  This implementation will hand off
 * callbacks to UI thread automatically.
 *
 * @author Ray Tsang
 */
public class AndroidDeferredManager extends DefaultDeferredManager {
	private static Void[] EMPTY_PARAMS = new Void[]{};

	public AndroidDeferredManager() {
		super();
	}

	public AndroidDeferredManager(ExecutorService executorService) {
		super(executorService);
	}

	/**
	 * Return a {@link Promise} for the {@link DeferredAsyncTask}.
	 * This can also automatically execute the task in background depending on
	 * {@link DeferredAsyncTask#getStartPolicy()} and/or {@link DefaultDeferredManager#isAutoSubmit()}.
	 * <p>
	 * Prior to Android Honeycomb (API 11), {@link AsyncTask#execute(Object...)} would be
	 * executed in the background concurrently in a thread pool, but starting with Honeycomb,
	 * {@link AsyncTask#execute(Object...)} will execute the background task serially.  To achieve
	 * older behavior, developer need to use {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}.
	 * <p>
	 * This method will always execute task in background concurrently if the task should be executed/submitted automatically.
	 * Hence, when using this method on Android API prior to Honeycomb, the task will be executed
	 * using {@link AsyncTask#execute(Object...)}.  On Android API version starting from Honeycomb,
	 * this method will execute with @see {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}
	 * using {@link Executor} from {@link #getExecutorService()}
	 *
	 * @param task {@link DeferredAsyncTask} to run in the background
	 *
	 * @return {@link DeferredAsyncTask#promise()}
	 *
	 * @see {@link AsyncTask#execute(Object...)}
	 * @see {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}
	 */
	@SuppressLint("NewApi")
	public <Progress, Result> Promise<Result, Throwable, Progress> when(
		DeferredAsyncTask<Void, Progress, Result> task) {

		if (task.getStartPolicy() == StartPolicy.AUTO
			|| (task.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(getExecutorService(), EMPTY_PARAMS);
			} else {
				task.execute(EMPTY_PARAMS);
			}
		}

		return task.promise();
	}

	@SuppressWarnings("rawtypes")
	public Promise<MultipleResults, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, ?>... tasks) {
		assertNotEmpty(tasks);

		Promise[] promises = new Promise[tasks.length];

		for (int i = 0; i < tasks.length; i++) {
			promises[i] = when(tasks[i]);
		}

		switch (promises.length) {
			case 2: return when(promises[0], promises[1]);
			case 3: return when(promises[0], promises[1], promises[2]);
			case 4: return when(promises[0], promises[1], promises[2], promises[3]);
			case 5: return when(promises[0], promises[1], promises[2], promises[3], promises[4]);
			default:
				Promise[] promiseN = new Promise[promises.length - 5];
				System.arraycopy(promises, 5, promiseN, 0, promiseN.length);
				return when(promises[0], promises[1], promises[2], promises[3], promises[4], promises[5], promiseN);
		}
	}

	@SuppressWarnings("rawtypes")
	public Promise<MultipleResults, OneReject<?>, MasterProgress> when(AndroidExecutionScope scope,
	                                                                   DeferredAsyncTask<Void, ?, ?>... tasks) {
		assertNotEmpty(tasks);

		Promise[] promises = new Promise[tasks.length];

		for (int i = 0; i < tasks.length; i++) {
			promises[i] = when(tasks[i]);
		}

		switch (promises.length) {
			case 2: return when(scope, promises[0], promises[1]);
			case 3: return when(scope, promises[0], promises[1], promises[2]);
			case 4: return when(scope, promises[0], promises[1], promises[2], promises[3]);
			case 5: return when(scope, promises[0], promises[1], promises[2], promises[3], promises[4]);
			default:
				Promise[] promiseN = new Promise[promises.length - 5];
				System.arraycopy(promises, 5, promiseN, 0, promiseN.length);
				return when(scope, promises[0], promises[1], promises[2], promises[3], promises[4], promises[5], promiseN);
		}
	}

	/**
	 * Wrap with {@link AndroidDeferredObject} so that callbacks can be executed in UI thread.
	 */
	@Override
	public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> task) {
		return new AndroidDeferredObject<D, Throwable, P>(super.when(task)).promise();
	}

	/**
	 * If a non-Android friendly promise is passed in, wrap it with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the UI thread.
	 */
	@Override
	public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
		if (promise instanceof AndroidDeferredObject) {
			return promise;
		}
		return new AndroidDeferredObject<D, F, P>(promise).promise();
	}

	/**
	 * If a non-Android friendly promise is passed in, wrap it with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @param scope   Whether to execute in UI thread or Background thread
	 * @param promise A promise
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise, AndroidExecutionScope scope) {
		if (promise instanceof AndroidDeferredObject) {
			return promise;
		}
		return new AndroidDeferredObject<D, F, P>(promise, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject2} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> when(Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2) {
		Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2);
		return new AndroidDeferredObject<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject3} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> when(Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3) {
		Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3);
		return new AndroidDeferredObject<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject4} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> when(Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4) {
		Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4);
		return new AndroidDeferredObject<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject5} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4, Promise<? extends V5, ?, ?> promiseV5) {
		Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5);
		return new AndroidDeferredObject<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObjectN} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4, Promise<? extends V5, ?, ?> promiseV5, Promise<?, ?, ?> promise6, Promise<?, ?, ?>... promises) {
		Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5, promise6, promises);
		return new AndroidDeferredObject<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject2} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2) {
		Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2);
		return new AndroidDeferredObject<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject3} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3) {
		Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3);
		return new AndroidDeferredObject<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject4} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4) {
		Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4);
		return new AndroidDeferredObject<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObject5} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4, Promise<? extends V5, ?, ?> promiseV5) {
		Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5);
		return new AndroidDeferredObject<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred.impl.MasterDeferredObjectN} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<? extends V1, ?, ?> promiseV1, Promise<? extends V2, ?, ?> promiseV2, Promise<? extends V3, ?, ?> promiseV3, Promise<? extends V4, ?, ?> promiseV4, Promise<? extends V5, ?, ?> promiseV5, Promise<?, ?, ?> promise6, Promise<?, ?, ?>... promises) {
		Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5, promise6, promises);
		return new AndroidDeferredObject<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p, scope).promise();
	}
}
