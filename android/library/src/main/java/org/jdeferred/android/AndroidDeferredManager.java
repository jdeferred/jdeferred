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

import java.util.concurrent.*;

import org.jdeferred.*;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MasterDeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneReject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

/**
 * This DeferredManager is designed to execute deferred tasks in the background,
 * but also executes callbacks (e.g., done, fail, progress, and always) in the UI thread.
 * This is important because only UI thread executions can update UI elements!
 * 
 * You can use {@link DeferredAsyncTask} to write in the more familiar Android {@link AsyncTask} API
 * and still being able to take advantage of {@link Promise} chaining.
 * 
 * Even more powerful, you can also use {@link Promise}, {@link Runnable}, {@link Callable},
 * and any other types supported by {@link DeferredManager}.  This implementation will hand off
 * callbacks to UI thread automatically.
 * 
 * @author Ray Tsang
 *
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
	 * 
	 * Prior to Android Honeycomb (API 11), {@link AsyncTask#execute(Object...)} would be
	 * executed in the background concurrently in a thread pool, but starting with Honeycomb,
	 * {@link AsyncTask#execute(Object...)} will execute the background task serially.  To achieve
	 * older behavior, developer need to use {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}.
	 * 
	 * This method will always execute task in background concurrently if the task should be executed/submitted automatically.
	 * Hence, when using this method on Android API prior to Honeycomb, the task will be executed 
	 * using {@link AsyncTask#execute(Object...)}.  On Android API version starting from Honeycomb,
	 * this method will execute with @see {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}
	 * using {@link Executor} from {@link #getExecutorService()}
	 * 
	 * @param task {@link DeferredAsyncTask} to run in the background
	 * @return {@link DeferredAsyncTask#promise()}
	 * @see {@link AsyncTask#execute(Object...)}
	 * @see {@link AsyncTask#executeOnExecutor(java.util.concurrent.Executor, Object...)}
	 */
	@SuppressLint("NewApi")
	public <Progress, Result> AndroidPromise<Result, Throwable, Progress> when(
			DeferredAsyncTask<Void, Progress, Result> task) {
		
		if (task.getStartPolicy() == StartPolicy.AUTO 
				|| (task.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(getExecutorService(), EMPTY_PARAMS);
			} else {
				task.execute(EMPTY_PARAMS);
			}
		}
		
		return ((AndroidPromise<Result, Throwable, Progress>)task.promise());
	}
	
	@SuppressWarnings("rawtypes")
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(
			DeferredAsyncTask<Void, ?, ?> ... tasks) {
		assertNotEmpty(tasks);
		
		Promise[] promises = new Promise[tasks.length];
		
		for (int i = 0; i < tasks.length; i++) {
			promises[i] = when(tasks[i]);
		}
		
		return when(promises);
	}
	
	@SuppressWarnings("rawtypes")
    public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(AndroidExecutionScope scope,
            DeferredAsyncTask<Void, ?, ?> ... tasks) {
        assertNotEmpty(tasks);
        Promise[] promises = new Promise[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            promises[i] = when(tasks[i]);
        }
        return when(scope, promises);
    }

	/**
	 * Wrap with {@link AndroidDeferredObject} so that callbacks can be executed in UI thread.
	 * This method is called by a number of other when(...) methods.  Effectively, at least these
	 * methods will also be wrapped by {@link AndroidDeferredObject}:
	 * <ul>
	 * 	<li>{@link DeferredManager#when(Callable)}</li>
	 *  <li>{@link DeferredManager#when(Callable...)}</li>
	 *  <li>{@link DeferredManager#when(Runnable)}</li>
	 *  <li>{@link DeferredManager#when(Runnable..)}</li>
	 *  <li>{@link DeferredManager#when(java.util.concurrent.Future)}</li>
	 *  <li>{@link DeferredManager#when(java.util.concurrent.Future...)}</li>
	 *  <li>{@link DeferredManager#when(org.jdeferred.DeferredRunnable...)}</li>
	 *  <li>{@link DeferredManager#when(org.jdeferred.DeferredRunnable)}</li>
	 *  <li>{@link DeferredManager#when(org.jdeferred.DeferredCallable...)}</li>
	 *  <li>{@link DeferredManager#when(org.jdeferred.DeferredCallable)}</li>
	 *  <li>{@link DeferredManager#when(DeferredFutureTask...)}</li>
	 * </ul>
	 */
	@Override
	public <D, P> AndroidPromise<D, Throwable, P> when(DeferredFutureTask<D, P> task) {
		return asAndroidPromise(new AndroidDeferredObject<D, Throwable, P>(super.when(task)).promise());
	}
	
	/**
	 * If a non-Android friendly promise is passed in, wrap it with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the UI thread.
	 */
	@Override
	public <D, F, P> AndroidPromise<D, F, P> when(Promise<D, F, P> promise) {
		if (promise instanceof AndroidDeferredObject) {
			return asAndroidPromise(promise);
		}
		return asAndroidPromise(new AndroidDeferredObject<>(promise).promise());
	}


	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(AndroidPromise... promises) {
		assertNotEmpty(promises);
		return new AndroidMasterDeferredObject(promises).promise();
	}

	/**
	 * If a non-Android friendly promise is passed in, wrap it with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 * @param scope Whether to execute in UI thread or Background thread
	 * @param promise A promise
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
    public <D, F, P> AndroidPromise<D, F, P> when(Promise<D, F, P> promise, AndroidExecutionScope scope) {
        if (promise instanceof AndroidDeferredObject) {
            return asAndroidPromise(promise);
        }
        return asAndroidPromise(new AndroidDeferredObject<D, F, P>(promise, scope).promise());
    }


	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(Runnable... runnables) {
		return asAndroidPromise(super.when(runnables));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(Callable<?>... callables) {
		return asAndroidPromise(super.when(callables));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(DeferredRunnable<?>... runnables) {
		return asAndroidPromise(super.when(runnables));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(DeferredCallable<?, ?>... callables) {
		return asAndroidPromise(super.when(callables));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(DeferredFutureTask<?, ?>... tasks) {
		return asAndroidPromise(super.when(tasks));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(Future<?>... futures) {
		return asAndroidPromise(super.when(futures));
	}

	@Override
	public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(Promise... promises) {
		return asAndroidPromise(super.when(promises));
	}

	@Override
	public <P> AndroidPromise<Void, Throwable, P> when(DeferredRunnable<P> runnable) {
		return asAndroidPromise(super.when(runnable));
	}

	@Override
	public <D, P> AndroidPromise<D, Throwable, P> when(DeferredCallable<D, P> runnable) {
		return asAndroidPromise(super.when(runnable));
	}

	@Override
	public AndroidPromise<Void, Throwable, Void> when(Runnable runnable) {
		return asAndroidPromise(super.when(runnable));
	}

	@Override
	public <D> AndroidPromise<D, Throwable, Void> when(Callable<D> callable) {
		return asAndroidPromise(super.when(callable));
	}

	@Override
	public <D> AndroidPromise<D, Throwable, Void> when(final Future<D> future) {
		return asAndroidPromise(super.when(future));
	}

	/**
	 * Wraps {@link MasterDeferredObject} with  with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope. 
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	@SuppressWarnings({ "rawtypes" })
    public AndroidPromise<MultipleResults, OneReject, MasterProgress> when(AndroidExecutionScope scope, Promise... promises) {
        return asAndroidPromise(new AndroidDeferredObject<>
		(super.when(promises), scope).promise());
    }

	public static <D, F, P> AndroidPromise<D, F, P> asAndroidPromise(Promise <D, F, P> promise) {
		return ((AndroidPromise<D, F, P>)promise);
	}

	public static <D, F, P> AndroidDeferred<D, F, P> asAndroidDeferred(Deferred <D, F, P> deferred) {
		return ((AndroidDeferred<D, F, P>)deferred);
	}
}
