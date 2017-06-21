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
package org.jdeferred;

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
import java.util.concurrent.Future;

/**
 * {@link DeferredManager} is especially useful when dealing with asynchronous
 * tasks, either {@link Runnable} or {@link Callable} objects.
 * <p>
 * It's also very useful when you need to get callbacks from multiple
 * {@link Promise} objects.
 * <p>
 * <pre>
 * <code>
 * {@link DeferredManager} dm = new {@link DefaultDeferredManager}();
 *
 * {@link Promise} p1, p2, p3;
 * // p1 = ...; p2 = ...; p3 = ...;
 * dm.when(p1, p2, p3)
 *   .done(new DoneCallback() { ... })
 *   .fail(new FailCallback() { ... })
 *   .progress(new ProgressCallback() { ... })
 * </code>
 * </pre>
 * <p>
 * When dealing with async threads:
 * <p>
 * <pre>
 * <code>
 * dm.when(new Callable() { ... }, new Callable() { ... })
 *   .done(new DoneCallback() { ... })
 *   .fail(new FailCallback() { ... })
 * </code>
 * </pre>
 *
 * @author Ray Tsang
 * @see DefaultDeferredManager
 */
@SuppressWarnings({"rawtypes"})
public interface DeferredManager {
	enum StartPolicy {
		/**
		 * Let Deferred Manager to determine whether to start the task at its own
		 * discretion.
		 */
		DEFAULT,

		/**
		 * Tells Deferred Manager to automatically start the task
		 */
		AUTO,

		/**
		 * Tells Deferred Manager that this task will be manually started
		 *
		 * @deprecated As of Version 1.2.5, this element is deprecated.
		 * Use MANUAL instead.
		 * It will be removed in version 1.3
		 */
		@Deprecated
		MANAUL,

		/**
		 * Tells Deferred Manager that this task will be manually started
		 */
		MANUAL
	}

	/**
	 * Simply returns the promise.
	 *
	 * @param promise
	 *
	 * @return promise
	 */
	<D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}.
	 *
	 * @param runnable
	 *
	 * @return {@link #when(DeferredFutureTask)}
	 *
	 * @see #when(DeferredFutureTask)
	 */
	Promise<Void, Throwable, Void> when(Runnable runnable);

	/**
	 * Wraps {@link Callable} with {@link DeferredFutureTask}
	 *
	 * @param callable
	 *
	 * @return {@link #when(DeferredFutureTask)}
	 *
	 * @see #when(DeferredFutureTask)
	 */
	<D> Promise<D, Throwable, Void> when(Callable<D> callable);

	/**
	 * Wraps {@link Future} and waits for {@link Future#get()} to return a result
	 * in the background.
	 *
	 * @param future
	 *
	 * @return {@link #when(Callable)}
	 */
	<D> Promise<D, Throwable, Void> when(Future<D> future);

	/**
	 * Wraps {@link DeferredRunnable} with {@link DeferredFutureTask}
	 *
	 * @param runnable
	 *
	 * @return {@link #when(DeferredFutureTask)}
	 *
	 * @see #when(DeferredFutureTask)
	 */
	<P> Promise<Void, Throwable, P> when(
		DeferredRunnable<P> runnable);

	/**
	 * Wraps {@link DeferredCallable} with {@link DeferredFutureTask}
	 *
	 * @param callable
	 *
	 * @return {@link #when(DeferredFutureTask)}
	 *
	 * @see #when(DeferredFutureTask)
	 */
	<D, P> Promise<D, Throwable, P> when(
		DeferredCallable<D, P> callable);

	/**
	 * May or may not submit {@link DeferredFutureTask} for execution. See
	 * implementation documentation.
	 *
	 * @param task
	 *
	 * @return {@link DeferredFutureTask#promise()}
	 */
	<D, P> Promise<D, Throwable, P> when(
		DeferredFutureTask<D, P> task);

	<F, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<F>, MasterProgress> when(
		Promise<? extends V1, ?, ?> promiseV1,
		Promise<? extends V2, ?, ?> promiseV2);

	<F, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<F>, MasterProgress> when(
		Promise<? extends V1, ?, ?> promiseV1,
		Promise<? extends V2, ?, ?> promiseV2,
		Promise<? extends V3, ?, ?> promiseV3);

	<F, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<F>, MasterProgress> when(
		Promise<? extends V1, ?, ?> promiseV1,
		Promise<? extends V2, ?, ?> promiseV2,
		Promise<? extends V3, ?, ?> promiseV3,
		Promise<? extends V4, ?, ?> promiseV4);

	<F, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(
		Promise<? extends V1, ?, ?> promiseV1,
		Promise<? extends V2, ?, ?> promiseV2,
		Promise<? extends V3, ?, ?> promiseV3,
		Promise<? extends V4, ?, ?> promiseV4,
		Promise<? extends V5, ?, ?> promiseV5);

	<F, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(
		Promise<? extends V1, ?, ?> promiseV1,
		Promise<? extends V2, ?, ?> promiseV2,
		Promise<? extends V3, ?, ?> promiseV3,
		Promise<? extends V4, ?, ?> promiseV4,
		Promise<? extends V5, ?, ?> promiseV5,
		Promise<?, ?, ?> promise6,
		Promise<?, ?, ?>... promises);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}
	 *
	 * @param runnable1
	 * @param runnable2
	 * @param runnables
	 *
	 * @see #when(DeferredFutureTask)
	 * @see #when(DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask...)
	 */
	Promise<MultipleResults, OneReject<Throwable>, MasterProgress> when(
		Runnable runnable1, Runnable runnable2, Runnable... runnables);

	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		Callable<? extends V1> callableV1,
		Callable<? extends V2> callableV2);

	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		Callable<? extends V1> callableV1,
		Callable<? extends V2> callableV2,
		Callable<? extends V3> callableV3);

	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		Callable<? extends V1> callableV1,
		Callable<? extends V2> callableV2,
		Callable<? extends V3> callableV3,
		Callable<? extends V4> callableV4);

	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Callable<? extends V1> callableV1,
		Callable<? extends V2> callableV2,
		Callable<? extends V3> callableV3,
		Callable<? extends V4> callableV4,
		Callable<? extends V5> callableV5);

	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Callable<? extends V1> callableV1,
		Callable<? extends V2> callableV2,
		Callable<? extends V3> callableV3,
		Callable<? extends V4> callableV4,
		Callable<? extends V5> callableV5,
		Callable<?> callable6,
		Callable<?>... callables);

	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<? extends V1> runnableV1,
		DeferredRunnable<? extends V2> runnableV2);

	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<? extends V1> runnableV1,
		DeferredRunnable<? extends V2> runnableV2,
		DeferredRunnable<? extends V3> runnableV3);

	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<? extends V1> runnableV1,
		DeferredRunnable<? extends V2> runnableV2,
		DeferredRunnable<? extends V3> runnableV3,
		DeferredRunnable<? extends V4> runnableV4);

	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<? extends V1> runnableV1,
		DeferredRunnable<? extends V2> runnableV2,
		DeferredRunnable<? extends V3> runnableV3,
		DeferredRunnable<? extends V4> runnableV4,
		DeferredRunnable<? extends V5> runnableV5);

	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<? extends V1> runnableV1,
		DeferredRunnable<? extends V2> runnableV2,
		DeferredRunnable<? extends V3> runnableV3,
		DeferredRunnable<? extends V4> runnableV4,
		DeferredRunnable<? extends V5> runnableV5,
		DeferredRunnable<?> runnable6,
		DeferredRunnable<?>... runnables);

	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<? extends V1, ?> callableV1,
		DeferredCallable<? extends V2, ?> callableV2);

	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<? extends V1, ?> callableV1,
		DeferredCallable<? extends V2, ?> callableV2,
		DeferredCallable<? extends V3, ?> callableV3);

	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<? extends V1, ?> callableV1,
		DeferredCallable<? extends V2, ?> callableV2,
		DeferredCallable<? extends V3, ?> callableV3,
		DeferredCallable<? extends V4, ?> callableV4);

	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<? extends V1, ?> callableV1,
		DeferredCallable<? extends V2, ?> callableV2,
		DeferredCallable<? extends V3, ?> callableV3,
		DeferredCallable<? extends V4, ?> callableV4,
		DeferredCallable<? extends V5, ?> callableV5);

	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<? extends V1, ?> callableV1,
		DeferredCallable<? extends V2, ?> callableV2,
		DeferredCallable<? extends V3, ?> callableV3,
		DeferredCallable<? extends V4, ?> callableV4,
		DeferredCallable<? extends V5, ?> callableV5,
		DeferredCallable<?, ?> callable6,
		DeferredCallable<?, ?>... callables);

	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<? extends V1, ?> taskV1,
		DeferredFutureTask<? extends V2, ?> taskV2);

	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<? extends V1, ?> taskV1,
		DeferredFutureTask<? extends V2, ?> taskV2,
		DeferredFutureTask<? extends V3, ?> taskV3);

	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<? extends V1, ?> taskV1,
		DeferredFutureTask<? extends V2, ?> taskV2,
		DeferredFutureTask<? extends V3, ?> taskV3,
		DeferredFutureTask<? extends V4, ?> taskV4);

	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<? extends V1, ?> taskV1,
		DeferredFutureTask<? extends V2, ?> taskV2,
		DeferredFutureTask<? extends V3, ?> taskV3,
		DeferredFutureTask<? extends V4, ?> taskV4,
		DeferredFutureTask<? extends V5, ?> taskV5);

	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<? extends V1, ?> taskV1,
		DeferredFutureTask<? extends V2, ?> taskV2,
		DeferredFutureTask<? extends V3, ?> taskV3,
		DeferredFutureTask<? extends V4, ?> taskV4,
		DeferredFutureTask<? extends V5, ?> taskV5,
		DeferredFutureTask<?, ?> task6,
		DeferredFutureTask<?, ?>... tasks);

	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		Future<? extends V1> futureV1,
		Future<? extends V2> futureV2);

	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		Future<? extends V1> futureV1,
		Future<? extends V2> futureV2,
		Future<? extends V3> futureV3);

	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		Future<? extends V1> futureV1,
		Future<? extends V2> futureV2,
		Future<? extends V3> futureV3,
		Future<? extends V4> futureV4);

	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Future<? extends V1> futureV1,
		Future<? extends V2> futureV2,
		Future<? extends V3> futureV3,
		Future<? extends V4> futureV4,
		Future<? extends V5> futureV5);

	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Future<? extends V1> futureV1,
		Future<? extends V2> futureV2,
		Future<? extends V3> futureV3,
		Future<? extends V4> futureV4,
		Future<? extends V5> futureV5,
		Future<?> future6,
		Future<?>... futures);
}
