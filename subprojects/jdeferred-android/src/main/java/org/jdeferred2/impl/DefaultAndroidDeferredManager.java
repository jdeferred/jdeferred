package org.jdeferred2.impl;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DeferredManager;
import org.jdeferred2.Promise;
import org.jdeferred2.android.AndroidDeferredObject;
import org.jdeferred2.android.AndroidExecutionScope;
import org.jdeferred2.android.DeferredAsyncTask;
import org.jdeferred2.multiple.MasterProgress;
import org.jdeferred2.multiple.MultipleResults;
import org.jdeferred2.multiple.MultipleResults2;
import org.jdeferred2.multiple.MultipleResults3;
import org.jdeferred2.multiple.MultipleResults4;
import org.jdeferred2.multiple.MultipleResults5;
import org.jdeferred2.multiple.MultipleResultsN;
import org.jdeferred2.multiple.OneReject;

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
 * <strong>IMPLEMENTATION NOTE:</strong> relies on package protected classes.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 */
public abstract class DefaultAndroidDeferredManager extends DefaultDeferredManager {
	private static Void[] EMPTY_PARAMS = new Void[]{};

	public DefaultAndroidDeferredManager() {
		super();
	}

	public DefaultAndroidDeferredManager(ExecutorService executorService) {
		super(executorService);
	}

	public <V1, V2> Promise<MultipleResults2, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		return new MasterDeferredObject2(when(taskV1), when(taskV2));
	}

	public <V1, V2, V3> Promise<MultipleResults3, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		return new MasterDeferredObject3(when(taskV1), when(taskV2), when(taskV3));
	}

	public <V1, V2, V3, V4> Promise<MultipleResults4, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		return new MasterDeferredObject4(when(taskV1), when(taskV2), when(taskV3), when(taskV4));
	}

	public <V1, V2, V3, V4, V5> Promise<MultipleResults5, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4,
		DeferredAsyncTask<Void, ?, V5> taskV5) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		assertNotNull(taskV5, TASK_V5);
		return new MasterDeferredObject5(when(taskV1), when(taskV2), when(taskV3), when(taskV4), when(taskV5));
	}

	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN, OneReject<?>, MasterProgress> when(
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4,
		DeferredAsyncTask<Void, ?, V5> taskV5,
		DeferredAsyncTask<Void, ?, ?> task6,
		DeferredAsyncTask<Void, ?, ?>... tasks) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		assertNotNull(taskV5, TASK_V5);
		assertNotNull(task6, "task6");

		Promise<V1, Throwable, ?> promise1 = when(taskV1);
		Promise<V2, Throwable, ?> promise2 = when(taskV2);
		Promise<V3, Throwable, ?> promise3 = when(taskV3);
		Promise<V4, Throwable, ?> promise4 = when(taskV4);
		Promise<V5, Throwable, ?> promise5 = when(taskV5);
		Promise<?, Throwable, ?> promise6 = when(task6);

		Promise[] promiseN = new Promise[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			promiseN[i] = when(tasks[i]);
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, promise6, promiseN);
	}

	public <V1, V2> Promise<MultipleResults2, OneReject<?>, MasterProgress> when(
		AndroidExecutionScope scope,
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		return new MasterDeferredObject2(when(taskV1, scope), when(taskV2, scope));
	}

	public <V1, V2, V3> Promise<MultipleResults3, OneReject<?>, MasterProgress> when(
		AndroidExecutionScope scope,
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		return new MasterDeferredObject3(when(taskV1, scope), when(taskV2, scope), when(taskV3, scope));
	}

	public <V1, V2, V3, V4> Promise<MultipleResults4, OneReject<?>, MasterProgress> when(
		AndroidExecutionScope scope,
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		return new MasterDeferredObject4(when(taskV1, scope), when(taskV2, scope), when(taskV3, scope), when(taskV4, scope));
	}

	public <V1, V2, V3, V4, V5> Promise<MultipleResults5, OneReject<?>, MasterProgress> when(
		AndroidExecutionScope scope,
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4,
		DeferredAsyncTask<Void, ?, V5> taskV5) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		assertNotNull(taskV5, TASK_V5);
		return new MasterDeferredObject5(when(taskV1, scope), when(taskV2, scope), when(taskV3, scope), when(taskV4, scope), when(taskV5, scope));
	}

	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN, OneReject<?>, MasterProgress> when(
		AndroidExecutionScope scope,
		DeferredAsyncTask<Void, ?, V1> taskV1,
		DeferredAsyncTask<Void, ?, V2> taskV2,
		DeferredAsyncTask<Void, ?, V3> taskV3,
		DeferredAsyncTask<Void, ?, V4> taskV4,
		DeferredAsyncTask<Void, ?, V5> taskV5,
		DeferredAsyncTask<Void, ?, ?> task6,
		DeferredAsyncTask<Void, ?, ?>... tasks) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		assertNotNull(taskV5, TASK_V5);
		assertNotNull(task6, "task6");

		Promise<V1, Throwable, ?> promise1 = when(taskV1, scope);
		Promise<V2, Throwable, ?> promise2 = when(taskV2, scope);
		Promise<V3, Throwable, ?> promise3 = when(taskV3, scope);
		Promise<V4, Throwable, ?> promise4 = when(taskV4, scope);
		Promise<V5, Throwable, ?> promise5 = when(taskV5, scope);
		Promise<?, Throwable, ?> promise6 = when(task6, scope);

		Promise[] promiseN = new Promise[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			promiseN[i] = when(tasks[i], scope);
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, promise6, promiseN);
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

	public <Progress, Result> Promise<Result, Throwable, Progress> when(
		DeferredAsyncTask<Void, Progress, Result> task, AndroidExecutionScope scope) {
		return when(when(task.promise()), scope);
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
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject2} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2) {
		Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2);
		return new AndroidDeferredObject<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject3} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3) {
		Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3);
		return new AndroidDeferredObject<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject4} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4) {
		Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4);
		return new AndroidDeferredObject<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject5} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5) {
		Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5);
		return new AndroidDeferredObject<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObjectN} with {@link AndroidDeferredObject} so that callbacks can
	 * be executed in UI thread.
	 */
	@Override
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5, Promise<?, ?, ?> promise6, Promise<?, ?, ?>... promises) {
		Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5, promise6, promises);
		return new AndroidDeferredObject<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject2} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2) {
		Promise<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2);
		return new AndroidDeferredObject<MultipleResults2<V1, V2>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject3} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3) {
		Promise<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3);
		return new AndroidDeferredObject<MultipleResults3<V1, V2, V3>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject4} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4) {
		Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4);
		return new AndroidDeferredObject<MultipleResults4<V1, V2, V3, V4>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObject5} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5) {
		Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5);
		return new AndroidDeferredObject<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	/**
	 * Wraps {@link org.jdeferred2.impl.MasterDeferredObjectN} with {@link AndroidDeferredObject}
	 * so that callbacks can be executed in the corresponding execution scope.
	 *
	 * @return A promise wrapped in @{link AndroidDeferredObject}
	 */
	public <R, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> when(AndroidExecutionScope scope, Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5, Promise<?, ?, ?> promise6, Promise<?, ?, ?>... promises) {
		Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress> p = super.when(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5, promise6, promises);
		return new AndroidDeferredObject<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<R>, MasterProgress>(p, scope).promise();
	}

	@Override
	protected boolean canPromise(Object o) {
		if (o instanceof DeferredAsyncTask) {
			return true;
		}
		return super.canPromise(o);
	}

	@Override
	protected Promise<?, ?, ?> toPromise(Object o) {
		if (o instanceof DeferredAsyncTask) {
			return when((DeferredAsyncTask) o);
		}
		return super.toPromise(o);
	}
}
