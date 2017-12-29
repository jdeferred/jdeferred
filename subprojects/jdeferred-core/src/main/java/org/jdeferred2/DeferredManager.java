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
package org.jdeferred2;

import org.jdeferred2.impl.DefaultDeferredManager;
import org.jdeferred2.multiple.AllValues;
import org.jdeferred2.multiple.MasterProgress;
import org.jdeferred2.multiple.MultipleResults;
import org.jdeferred2.multiple.MultipleResults2;
import org.jdeferred2.multiple.MultipleResults3;
import org.jdeferred2.multiple.MultipleResults4;
import org.jdeferred2.multiple.MultipleResults5;
import org.jdeferred2.multiple.MultipleResultsN;
import org.jdeferred2.multiple.OneReject;
import org.jdeferred2.multiple.OneResult;

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

	/**
	 * Submits 2 {@code Promise}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the promises rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param promiseV1 the first promise to be resolved
	 * @param promiseV2 the second promise to be resolved
	 * @param <F>       the common type the promises may reject
	 * @param <V1>      the resolve type of the first promise
	 * @param <V2>      the resolve type of the second promise
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<F, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<F>, MasterProgress> when(
		Promise<V1, ?, ?> promiseV1,
		Promise<V2, ?, ?> promiseV2);

	/**
	 * Submits 3 {@code Promise}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the promises rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param promiseV1 the first promise to be resolved
	 * @param promiseV2 the second promise to be resolved
	 * @param promiseV3 the third promise to be resolved
	 * @param <F>       the common type the promises may reject
	 * @param <V1>      the resolve type of the first promise
	 * @param <V2>      the resolve type of the second promise
	 * @param <V3>      the resolve type of the third promise
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<F, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<F>, MasterProgress> when(
		Promise<V1, ?, ?> promiseV1,
		Promise<V2, ?, ?> promiseV2,
		Promise<V3, ?, ?> promiseV3);

	/**
	 * Submits 4 {@code Promise}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the promises rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param promiseV1 the first promise to be resolved
	 * @param promiseV2 the second promise to be resolved
	 * @param promiseV3 the third promise to be resolved
	 * @param promiseV4 the fourth promise to be resolved
	 * @param <F>       the common type the promises may reject
	 * @param <V1>      the resolve type of the first promise
	 * @param <V2>      the resolve type of the second promise
	 * @param <V3>      the resolve type of the third promise
	 * @param <V4>      the resolve type of the fourth promise
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<F, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<F>, MasterProgress> when(
		Promise<V1, ?, ?> promiseV1,
		Promise<V2, ?, ?> promiseV2,
		Promise<V3, ?, ?> promiseV3,
		Promise<V4, ?, ?> promiseV4);

	/**
	 * Submits 5 {@code Promise}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the promises rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param promiseV1 the first promise to be resolved
	 * @param promiseV2 the second promise to be resolved
	 * @param promiseV3 the third promise to be resolved
	 * @param promiseV4 the fourth promise to be resolved
	 * @param promiseV5 the fifth promise to be resolved
	 * @param <F>       the common type the promises may reject
	 * @param <V1>      the resolve type of the first promise
	 * @param <V2>      the resolve type of the second promise
	 * @param <V3>      the resolve type of the third promise
	 * @param <V4>      the resolve type of the fourth promise
	 * @param <V5>      the resolve type of the fifth promise
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<F, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(
		Promise<V1, ?, ?> promiseV1,
		Promise<V2, ?, ?> promiseV2,
		Promise<V3, ?, ?> promiseV3,
		Promise<V4, ?, ?> promiseV4,
		Promise<V5, ?, ?> promiseV5);

	/**
	 * Submits {@code N} {@code Promise}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the promises rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param promiseV1 the first promise to be resolved
	 * @param promiseV2 the second promise to be resolved
	 * @param promiseV3 the third promise to be resolved
	 * @param promiseV4 the fourth promise to be resolved
	 * @param promiseV5 the fifth promise to be resolved
	 * @param promises  additional promises to be resolved
	 * @param <F>       the common type the promises may reject
	 * @param <V1>      the resolve type of the first promise
	 * @param <V2>      the resolve type of the second promise
	 * @param <V3>      the resolve type of the third promise
	 * @param <V4>      the resolve type of the fourth promise
	 * @param <V5>      the resolve type of the fifth promise
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<F, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(
		Promise<V1, ?, ?> promiseV1,
		Promise<V2, ?, ?> promiseV2,
		Promise<V3, ?, ?> promiseV3,
		Promise<V4, ?, ?> promiseV4,
		Promise<V5, ?, ?> promiseV5,
		Promise<?, ?, ?> promise6,
		Promise<?, ?, ?>... promises);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}
	 *
	 * @param runnable1 the first runnable
	 * @param runnable2 the second runnable
	 * @param runnables additional runnables
	 *
	 * @see #when(DeferredFutureTask)
	 * @see #when(DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask,)
	 * @see #when(DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask, DeferredFutureTask...)
	 */
	Promise<MultipleResults, OneReject<Throwable>, MasterProgress> when(
		Runnable runnable1, Runnable runnable2, Runnable... runnables);

	/**
	 * Submits 2 {@code Callable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		Callable<V1> callableV1,
		Callable<V2> callableV2);

	/**
	 * Submits 3 {@code Callable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		Callable<V1> callableV1,
		Callable<V2> callableV2,
		Callable<V3> callableV3);

	/**
	 * Submits 4 {@code Callable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		Callable<V1> callableV1,
		Callable<V2> callableV2,
		Callable<V3> callableV3,
		Callable<V4> callableV4);

	/**
	 * Submits 5 {@code Callable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param callableV5 the fifth callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 * @param <V5>       the resolve type of the fifth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Callable<V1> callableV1,
		Callable<V2> callableV2,
		Callable<V3> callableV3,
		Callable<V4> callableV4,
		Callable<V5> callableV5);

	/**
	 * Submits {@code N} {@code Callable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param callableV5 the fifth callable to be resolved
	 * @param callables  additional callables to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 * @param <V5>       the resolve type of the fifth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Callable<V1> callableV1,
		Callable<V2> callableV2,
		Callable<V3> callableV3,
		Callable<V4> callableV4,
		Callable<V5> callableV5,
		Callable<?> callable6,
		Callable<?>... callables);

	/**
	 * Submits 2 {@link DeferredRunnable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the runnables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param runnableP1 the first runnable to be resolved
	 * @param runnableP2 the second runnable to be resolved
	 * @param <P1>       the progress type of the first runnable
	 * @param <P2>       the progress type of the second runnable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<P1, P2> Promise<MultipleResults2<Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2);

	/**
	 * Submits 3 {@link DeferredRunnable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the runnables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param runnableP1 the first runnable to be resolved
	 * @param runnableP2 the second runnable to be resolved
	 * @param runnableP3 the third runnable to be resolved
	 * @param <P1>       the progress type of the first runnable
	 * @param <P2>       the progress type of the second runnable
	 * @param <P3>       the progress type of the third runnable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<P1, P2, P3> Promise<MultipleResults3<Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3);

	/**
	 * Submits 4 {@link DeferredRunnable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the runnables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param runnableP1 the first runnable to be resolved
	 * @param runnableP2 the second runnable to be resolved
	 * @param runnableP3 the third runnable to be resolved
	 * @param runnableP4 the fourth runnable to be resolved
	 * @param <P1>       the progress type of the first runnable
	 * @param <P2>       the progress type of the second runnable
	 * @param <P3>       the progress type of the third runnable
	 * @param <P4>       the progress type of the fourth runnable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<P1, P2, P3, P4> Promise<MultipleResults4<Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4);

	/**
	 * Submits 5 {@link DeferredRunnable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the runnables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param runnableP1 the first runnable to be resolved
	 * @param runnableP2 the second runnable to be resolved
	 * @param runnableP3 the third runnable to be resolved
	 * @param runnableP4 the fourth runnable to be resolved
	 * @param runnableP5 the fifth runnable to be resolved
	 * @param <P1>       the progress type of the first runnable
	 * @param <P2>       the progress type of the second runnable
	 * @param <P3>       the progress type of the third runnable
	 * @param <P4>       the progress type of the fourth runnable
	 * @param <P5>       the progress type of the fifth runnable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<P1, P2, P3, P4, P5> Promise<MultipleResults5<Void, Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4,
		DeferredRunnable<P5> runnableP5);

	/**
	 * Submits {@code N} {@link DeferredRunnable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the runnables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param runnableP1 the first runnable to be resolved
	 * @param runnableP2 the second runnable to be resolved
	 * @param runnableP3 the third runnable to be resolved
	 * @param runnableP4 the fourth runnable to be resolved
	 * @param runnableP5 the fifth runnable to be resolved
	 * @param runnables  additional runnables to be resolved
	 * @param <P1>       the progress type of the first runnable
	 * @param <P2>       the progress type of the second runnable
	 * @param <P3>       the progress type of the third runnable
	 * @param <P4>       the progress type of the fourth runnable
	 * @param <P5>       the progress type of the fifth runnable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<P1, P2, P3, P4, P5> Promise<MultipleResultsN<Void, Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4,
		DeferredRunnable<P5> runnableP5,
		DeferredRunnable<?> runnable6,
		DeferredRunnable<?>... runnables);

	/**
	 * Submits 2 {@link DeferredCallable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<V1, ?> callableV1,
		DeferredCallable<V2, ?> callableV2);

	/**
	 * Submits 3 {@link DeferredCallable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<V1, ?> callableV1,
		DeferredCallable<V2, ?> callableV2,
		DeferredCallable<V3, ?> callableV3);

	/**
	 * Submits 4 {@link DeferredCallable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<V1, ?> callableV1,
		DeferredCallable<V2, ?> callableV2,
		DeferredCallable<V3, ?> callableV3,
		DeferredCallable<V4, ?> callableV4);

	/**
	 * Submits 5 {@link DeferredCallable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param callableV5 the fifth callable to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 * @param <V5>       the resolve type of the fifth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<V1, ?> callableV1,
		DeferredCallable<V2, ?> callableV2,
		DeferredCallable<V3, ?> callableV3,
		DeferredCallable<V4, ?> callableV4,
		DeferredCallable<V5, ?> callableV5);

	/**
	 * Submits {@code N} {@link DeferredCallable}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the callables rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param callableV1 the first callable to be resolved
	 * @param callableV2 the second callable to be resolved
	 * @param callableV3 the third callable to be resolved
	 * @param callableV4 the fourth callable to be resolved
	 * @param callableV5 the fifth callable to be resolved
	 * @param callables  additional callables to be resolved
	 * @param <V1>       the resolve type of the first callable
	 * @param <V2>       the resolve type of the second callable
	 * @param <V3>       the resolve type of the third callable
	 * @param <V4>       the resolve type of the fourth callable
	 * @param <V5>       the resolve type of the fifth callable
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredCallable<V1, ?> callableV1,
		DeferredCallable<V2, ?> callableV2,
		DeferredCallable<V3, ?> callableV3,
		DeferredCallable<V4, ?> callableV4,
		DeferredCallable<V5, ?> callableV5,
		DeferredCallable<?, ?> callable6,
		DeferredCallable<?, ?>... callables);

	/**
	 * Submits 2 {@link DeferredFutureTask}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the tasks rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param taskV1 the first task to be resolved
	 * @param taskV2 the second task to be resolved
	 * @param <V1>   the resolve type of the first task
	 * @param <V2>   the resolve type of the second task
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<V1, ?> taskV1,
		DeferredFutureTask<V2, ?> taskV2);

	/**
	 * Submits 3 {@link DeferredFutureTask}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the tasks rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param taskV1 the first task to be resolved
	 * @param taskV2 the second task to be resolved
	 * @param taskV3 the third task to be resolved
	 * @param <V1>   the resolve type of the first task
	 * @param <V2>   the resolve type of the second task
	 * @param <V3>   the resolve type of the third task
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<V1, ?> taskV1,
		DeferredFutureTask<V2, ?> taskV2,
		DeferredFutureTask<V3, ?> taskV3);

	/**
	 * Submits 4 {@link DeferredFutureTask}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the tasks rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param taskV1 the first task to be resolved
	 * @param taskV2 the second task to be resolved
	 * @param taskV3 the third task to be resolved
	 * @param taskV4 the fourth task to be resolved
	 * @param <V1>   the resolve type of the first task
	 * @param <V2>   the resolve type of the second task
	 * @param <V3>   the resolve type of the third task
	 * @param <V4>   the resolve type of the fourth task
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<V1, ?> taskV1,
		DeferredFutureTask<V2, ?> taskV2,
		DeferredFutureTask<V3, ?> taskV3,
		DeferredFutureTask<V4, ?> taskV4);

	/**
	 * Submits 5 {@link DeferredFutureTask}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the tasks rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param taskV1 the first task to be resolved
	 * @param taskV2 the second task to be resolved
	 * @param taskV3 the third task to be resolved
	 * @param taskV4 the fourth task to be resolved
	 * @param taskV5 the fifth task to be resolved
	 * @param <V1>   the resolve type of the first task
	 * @param <V2>   the resolve type of the second task
	 * @param <V3>   the resolve type of the third task
	 * @param <V4>   the resolve type of the fourth task
	 * @param <V5>   the resolve type of the fifth task
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<V1, ?> taskV1,
		DeferredFutureTask<V2, ?> taskV2,
		DeferredFutureTask<V3, ?> taskV3,
		DeferredFutureTask<V4, ?> taskV4,
		DeferredFutureTask<V5, ?> taskV5);

	/**
	 * Submits {@code N} {@link DeferredFutureTask}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the tasks rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param taskV1 the first task to be resolved
	 * @param taskV2 the second task to be resolved
	 * @param taskV3 the third task to be resolved
	 * @param taskV4 the fourth task to be resolved
	 * @param taskV5 the fifth task to be resolved
	 * @param tasks  additional tasks to be resolved
	 * @param <V1>   the resolve type of the first task
	 * @param <V2>   the resolve type of the second task
	 * @param <V3>   the resolve type of the third task
	 * @param <V4>   the resolve type of the fourth task
	 * @param <V5>   the resolve type of the fifth task
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		DeferredFutureTask<V1, ?> taskV1,
		DeferredFutureTask<V2, ?> taskV2,
		DeferredFutureTask<V3, ?> taskV3,
		DeferredFutureTask<V4, ?> taskV4,
		DeferredFutureTask<V5, ?> taskV5,
		DeferredFutureTask<?, ?> task6,
		DeferredFutureTask<?, ?>... tasks);

	/**
	 * Submits 2 {@code Future}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the futures rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param futureV1 the first future to be resolved
	 * @param futureV2 the second future to be resolved
	 * @param <V1>     the resolve type of the first future
	 * @param <V2>     the resolve type of the second future
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(
		Future<V1> futureV1,
		Future<V2> futureV2);

	/**
	 * Submits 3 {@code Future}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the futures rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param futureV1 the first future to be resolved
	 * @param futureV2 the second future to be resolved
	 * @param futureV3 the third future to be resolved
	 * @param <V1>     the resolve type of the first future
	 * @param <V2>     the resolve type of the second future
	 * @param <V3>     the resolve type of the third future
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(
		Future<V1> futureV1,
		Future<V2> futureV2,
		Future<V3> futureV3);

	/**
	 * Submits 4 {@code Future}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the futures rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param futureV1 the first future to be resolved
	 * @param futureV2 the second future to be resolved
	 * @param futureV3 the third future to be resolved
	 * @param futureV4 the fourth future to be resolved
	 * @param <V1>     the resolve type of the first future
	 * @param <V2>     the resolve type of the second future
	 * @param <V3>     the resolve type of the third future
	 * @param <V4>     the resolve type of the fourth future
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(
		Future<V1> futureV1,
		Future<V2> futureV2,
		Future<V3> futureV3,
		Future<V4> futureV4);

	/**
	 * Submits 5 {@code Future}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the futures rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param futureV1 the first future to be resolved
	 * @param futureV2 the second future to be resolved
	 * @param futureV3 the third future to be resolved
	 * @param futureV4 the fourth future to be resolved
	 * @param futureV5 the fifth future to be resolved
	 * @param <V1>     the resolve type of the first future
	 * @param <V2>     the resolve type of the second future
	 * @param <V3>     the resolve type of the third future
	 * @param <V4>     the resolve type of the fourth future
	 * @param <V5>     the resolve type of the fifth future
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Future<V1> futureV1,
		Future<V2> futureV2,
		Future<V3> futureV3,
		Future<V4> futureV4,
		Future<V5> futureV5);

	/**
	 * Submits {@code N} {@code Future}s returns a combined {@link Promise}.
	 * The combined promise signals {@code fail} as soon as any of the futures rejects its value.
	 * The return type of the combined {@link Promise} contains all resolved values.
	 *
	 * @param futureV1 the first future to be resolved
	 * @param futureV2 the second future to be resolved
	 * @param futureV3 the third future to be resolved
	 * @param futureV4 the fourth future to be resolved
	 * @param futureV5 the fifth future to be resolved
	 * @param futures  additional futures to be resolved
	 * @param <V1>     the resolve type of the first future
	 * @param <V2>     the resolve type of the second future
	 * @param <V3>     the resolve type of the third future
	 * @param <V4>     the resolve type of the fourth future
	 * @param <V5>     the resolve type of the fifth future
	 *
	 * @return a combined {@link Promise}
	 *
	 * @since 2.0
	 */
	<V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(
		Future<V1> futureV1,
		Future<V2> futureV2,
		Future<V3> futureV3,
		Future<V4> futureV4,
		Future<V5> futureV5,
		Future<?> future6,
		Future<?>... futures);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first runnable does so.
	 * Wraps each {@code runnable} with {@link DeferredFutureTask}.
	 *
	 * @param runnableV1 a task to be executed. Must not be null
	 * @param runnableV2 a task to be executed. Must not be null
	 * @param runnables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		Runnable runnableV1,
		Runnable runnableV2,
		Runnable... runnables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first callable does so.
	 * Wraps each {@code callable} with {@link DeferredFutureTask}.
	 *
	 * @param callableV1 a task to be executed. Must not be null
	 * @param callableV2 a task to be executed. Must not be null
	 * @param callables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		Callable<?> callableV1,
		Callable<?> callableV2,
		Callable<?>... callables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first runnable does so.
	 * Wraps each {@code runnable} with {@link DeferredFutureTask}.
	 *
	 * @param runnableV1 a task to be executed. Must not be null
	 * @param runnableV2 a task to be executed. Must not be null
	 * @param runnables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		DeferredRunnable<?> runnableV1,
		DeferredRunnable<?> runnableV2,
		DeferredRunnable<?>... runnables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first callable does so.
	 * Wraps each {@code callable} with {@link DeferredFutureTask}.
	 *
	 * @param callableV1 a task to be executed. Must not be null
	 * @param callableV2 a task to be executed. Must not be null
	 * @param callables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		DeferredCallable<?, ?> callableV1,
		DeferredCallable<?, ?> callableV2,
		DeferredCallable<?, ?>... callables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first future does so.
	 * Wraps each {@code future} with {@link DeferredFutureTask}.
	 *
	 * @param futureV1 a task to be executed. Must not be null
	 * @param futureV2 a task to be executed. Must not be null
	 * @param futures  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		Future<?> futureV1,
		Future<?> futureV2,
		Future<?>... futures);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first task does so.
	 *
	 * @param taskV1 a task to be executed. Must not be null
	 * @param taskV2 a task to be executed. Must not be null
	 * @param tasks  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(
		DeferredFutureTask<?, ?> taskV1,
		DeferredFutureTask<?, ?> taskV2,
		DeferredFutureTask<?, ?>... tasks);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each runnable does so.
	 * Wraps each {@code runnable} with {@link DeferredFutureTask}.
	 *
	 * @param runnableV1 a task to be executed. Must not be null
	 * @param runnableV2 a task to be executed. Must not be null
	 * @param runnables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		Runnable runnableV1,
		Runnable runnableV2,
		Runnable... runnables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each callable does so.
	 * Wraps each {@code callable} with {@link DeferredFutureTask}.
	 *
	 * @param callableV1 a task to be executed. Must not be null
	 * @param callableV2 a task to be executed. Must not be null
	 * @param callables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		Callable<?> callableV1,
		Callable<?> callableV2,
		Callable<?>... callables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each runnable does so.
	 * Wraps each {@code runnable} with {@link DeferredFutureTask}.
	 *
	 * @param runnableV1 a task to be executed. Must not be null
	 * @param runnableV2 a task to be executed. Must not be null
	 * @param runnables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		DeferredRunnable<?> runnableV1,
		DeferredRunnable<?> runnableV2,
		DeferredRunnable<?>... runnables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each callable does so.
	 * Wraps each {@code callable} with {@link DeferredFutureTask}.
	 *
	 * @param callableV1 a task to be executed. Must not be null
	 * @param callableV2 a task to be executed. Must not be null
	 * @param callables  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		DeferredCallable<?, ?> callableV1,
		DeferredCallable<?, ?> callableV2,
		DeferredCallable<?, ?>... callables);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each future does so.
	 * Wraps each {@code future} with {@link DeferredFutureTask}.
	 *
	 * @param futureV1 a task to be executed. Must not be null
	 * @param futureV2 a task to be executed. Must not be null
	 * @param futures  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		Future<?> futureV1,
		Future<?> futureV2,
		Future<?>... futures);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each task does so.
	 *
	 * @param taskV1 a task to be executed. Must not be null
	 * @param taskV2 a task to be executed. Must not be null
	 * @param tasks  additional tasks to be executed. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all tasks.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		DeferredFutureTask<?, ?> taskV1,
		DeferredFutureTask<?, ?> taskV2,
		DeferredFutureTask<?, ?>... tasks);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each promise does so.
	 *
	 * @param promiseV1 a promise. Must not be null
	 * @param promiseV2 a promise. Must not be null
	 * @param promises  additional promises. May be null
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all promises.
	 *
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(
		Promise<?, ?, ?> promiseV1,
		Promise<?, ?, ?> promiseV2,
		Promise<?, ?, ?>... promises);

	/**
	 * Accept an iterable of a variety of different object types, and convert it into corresponding Promise. E.g.,
	 * if an item is a {@link Callable}, it'll call {@link #when(Callable)} to convert that into a Promise.
	 * <p>
	 * If the item is of an unknown type, it'll throw an {@link IllegalArgumentException}.
	 *
	 * @param iterable the source of tasks. Must be non-null and not empty. Every item must be convertible to {@link Promise}
	 *
	 * @return a composite {@link Promise} that rejects as soon as the first of the submitted tasks is rejected or contains
	 * the resolution of all given tasks.
	 *
	 * @throws IllegalArgumentException if any item in iterable cannot be converted to a {@link Promise}
	 * @since 2.0
	 */
	Promise<MultipleResults, OneReject<?>, MasterProgress> when(Iterable<?> iterable);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when the first task does so.
	 * If an item is a {@link Callable}, it'll call {@link #when(Callable)} to convert that into a Promise.
	 * <p>
	 * If the item is of an unknown type, it'll throw an {@link IllegalArgumentException}.
	 * <strong>WARNING: </strong>does not accept items of type {@code Promise}.
	 *
	 * @param iterable the source of tasks. Must be non-null and not empty. Every item must be convertible to {@link Promise}
	 *
	 * @return a composite {@link Promise} that resolves/rejects as soon as the first of the submitted tasks is resolved/rejected.
	 *
	 * @throws IllegalArgumentException if any item in iterable cannot be converted to a {@link Promise}
	 * @since 2.0
	 */
	Promise<OneResult<?>, OneReject<Throwable>, Void> race(Iterable<?> iterable);

	/**
	 * Creates a {@link Promise} that signals {@code done} or {@code reject} when each task does so.
	 * If an item is a {@link Callable}, it'll call {@link #when(Callable)} to convert that into a Promise.
	 * <p>
	 * If the item is of an unknown type, it'll throw an {@link IllegalArgumentException}.
	 *
	 * @param iterable the source of tasks. Must be non-null and not empty. Every item must be convertible to {@link Promise}
	 *
	 * @return a composite {@link Promise} that collects resolve/reject values from all promises.
	 *
	 * @throws IllegalArgumentException if any item in iterable cannot be converted to a {@link Promise}
	 * @since 2.0
	 */
	Promise<AllValues, Throwable, MasterProgress> settle(Iterable<?> iterable);

	/**
	 * A convenience method create a {@link Promise} that immediately resolves to a value.
	 *
	 * @param resolve value to resolve to
	 *
	 * @return a Promise that resolves to value
	 *
	 * @since 2.0
	 */
	<D, F, P> Promise<D, F, P> resolve(D resolve);

	/**
	 * A convenience method to create a {@link Promise} that immediately fails with a reason.
	 *
	 * @param reject reason to reject
	 *
	 * @return a {@link Promise} that rejects with reason
	 *
	 * @since 2.0
	 */
	<D, F, P> Promise<D, F, P> reject(F reject);
}
