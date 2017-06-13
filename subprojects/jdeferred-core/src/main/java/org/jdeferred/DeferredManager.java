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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MasterDeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.MultipleResults2;
import org.jdeferred.multiple.MultipleResults3;
import org.jdeferred.multiple.MultipleResults4;
import org.jdeferred.multiple.MultipleResults5;
import org.jdeferred.multiple.MultipleResultsN;
import org.jdeferred.multiple.OneReject;

/**
 * {@link DeferredManager} is especially useful when dealing with asynchronous
 * tasks, either {@link Runnable} or {@link Callable} objects.
 * 
 * It's also very useful when you need to get callbacks from multiple
 * {@link Promise} objects.
 * 
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
 * 
 * When dealing with async threads:
 * 
 * <pre>
 * <code>
 * dm.when(new Callable() { ... }, new Callable() { ... })
 *   .done(new DoneCallback() { ... })
 *   .fail(new FailCallback() { ... }) 
 * </code>
 * </pre>
 * 
 * @see DefaultDeferredManager
 * @see MasterDeferredObject
 * @author Ray Tsang
 * 
 */
@SuppressWarnings({ "rawtypes" })
public interface DeferredManager {
	public static enum StartPolicy {
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
		 * @deprecated  As of Version 1.2.5, this element is deprecated.
		 *      Use MANUAL instead. 
		 *      It will be removed in version 1.3
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
	 * @return promise
	 */
	public abstract <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}.
	 * 
	 * @see #when(DeferredFutureTask)
	 * @param runnable
	 * @return {@link #when(DeferredFutureTask)}
	 */
	public abstract Promise<Void, Throwable, Void> when(Runnable runnable);

	/**
	 * Wraps {@link Callable} with {@link DeferredFutureTask}
	 * 
	 * @see #when(DeferredFutureTask)
	 * @param callable
	 * @return {@link #when(DeferredFutureTask)}
	 */
	public abstract <D> Promise<D, Throwable, Void> when(Callable<D> callable);
	
	/**
	 * Wraps {@link Future} and waits for {@link Future#get()} to return a result
	 * in the background.
	 *  
	 * @param future
	 * @return {@link #when(Callable)}
	 */
	public abstract <D> Promise<D, Throwable, Void> when(Future<D> future);

	/**
	 * Wraps {@link DeferredRunnable} with {@link DeferredFutureTask}
	 * 
	 * @see #when(DeferredFutureTask)
	 * @param runnable
	 * @return {@link #when(DeferredFutureTask)}
	 */
	public abstract <P> Promise<Void, Throwable, P> when(
			DeferredRunnable<P> runnable);

	/**
	 * Wraps {@link DeferredCallable} with {@link DeferredFutureTask}
	 * 
	 * @see #when(DeferredFutureTask)
	 * @param callable
	 * @return {@link #when(DeferredFutureTask)}
	 */
	public abstract <D, P> Promise<D, Throwable, P> when(
			DeferredCallable<D, P> callable);

	/**
	 * May or may not submit {@link DeferredFutureTask} for execution. See
	 * implementation documentation.
	 * 
	 * @param task
	 * @return {@link DeferredFutureTask#promise()}
	 */
	public abstract <D, P> Promise<D, Throwable, P> when(
			DeferredFutureTask<D, P> task);

	/**
	 * This will return a special Promise called {@link MasterDeferredObject}. In
	 * short,
	 * <ul>
	 * <li>{@link Promise#done(DoneCallback)} will be triggered if all promises
	 * resolves (i.e., all finished successfully).</li>
	 * <li>{@link Promise#fail(FailCallback)} will be triggered if any promises
	 * rejects (i.e., if any one failed).</li>
	 * <li>{@link Promise#progress(ProgressCallback)} will be triggered whenever
	 * one promise resolves or rejects, or whenever a promise was notified
	 * progress.</li>
	 * <li>{@link Promise#always(AlwaysCallback)} will be triggered whenever
	 * {@link Promise#done(DoneCallback)} or {@link Promise#fail(FailCallback)}
	 * would be triggered</li>
	 * </ul>
	 * 
	 * @param promises
	 * @return {@link MasterDeferredObject}
	 */
	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			Promise... promises);
	*/

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		Promise<? extends A,?,?> promiseA,
		Promise<? extends B,?,?> promiseB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		Promise<? extends A,?,?> promiseA,
		Promise<? extends B,?,?> promiseB,
		Promise<? extends C,?,?> promiseC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		Promise<? extends A,?,?> promiseA,
		Promise<? extends B,?,?> promiseB,
		Promise<? extends C,?,?> promiseC,
		Promise<? extends D,?,?> promiseD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Promise<? extends A,?,?> promiseA,
		Promise<? extends B,?,?> promiseB,
		Promise<? extends C,?,?> promiseC,
		Promise<? extends D,?,?> promiseD,
		Promise<? extends E,?,?> promiseE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Promise<? extends A,?,?> promiseA,
		Promise<? extends B,?,?> promiseB,
		Promise<? extends C,?,?> promiseC,
		Promise<? extends D,?,?> promiseD,
		Promise<? extends E,?,?> promiseE,
		Promise<?,?,?> promise,
		Promise<?,?,?>... promises);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}
	 * 
	 * @param runnables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	public abstract <R> Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			Runnable... runnables);

	/**
	 * Wraps {@link Callable} with {@link DeferredFutureTask}
	 * 
	 * @param callables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			Callable<?>... callables);
	*/

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		Callable<? extends A> callableA,
		Callable<? extends B> callableB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		Callable<? extends A> callableA,
		Callable<? extends B> callableB,
		Callable<? extends C> callableC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		Callable<? extends A> callableA,
		Callable<? extends B> callableB,
		Callable<? extends C> callableC,
		Callable<? extends D> callableD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Callable<? extends A> callableA,
		Callable<? extends B> callableB,
		Callable<? extends C> callableC,
		Callable<? extends D> callableD,
		Callable<? extends E> callableE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Callable<? extends A> callableA,
		Callable<? extends B> callableB,
		Callable<? extends C> callableC,
		Callable<? extends D> callableD,
		Callable<? extends E> callableE,
		Callable<?> callable,
		Callable<?>... callables);

	/**
	 * Wraps {@link DeferredRunnable} with {@link DeferredFutureTask}
	 * 
	 * @param runnables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			DeferredRunnable<?>... runnables);
	*/

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		DeferredRunnable<? extends A> runnableA,
		DeferredRunnable<? extends B> runnableB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		DeferredRunnable<? extends A> runnableA,
		DeferredRunnable<? extends B> runnableB,
		DeferredRunnable<? extends C> runnableC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		DeferredRunnable<? extends A> runnableA,
		DeferredRunnable<? extends B> runnableB,
		DeferredRunnable<? extends C> runnableC,
		DeferredRunnable<? extends D> runnableD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredRunnable<? extends A> runnableA,
		DeferredRunnable<? extends B> runnableB,
		DeferredRunnable<? extends C> runnableC,
		DeferredRunnable<? extends D> runnableD,
		DeferredRunnable<? extends E> runnableE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredRunnable<? extends A> runnableA,
		DeferredRunnable<? extends B> runnableB,
		DeferredRunnable<? extends C> runnableC,
		DeferredRunnable<? extends D> runnableD,
		DeferredRunnable<? extends E> runnableE,
		DeferredRunnable<?> runnable,
		DeferredRunnable<?>... runnables);
	/**
	 * Wraps {@link DeferredCallable} with {@link DeferredFutureTask}
	 * 
	 * @param callables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			DeferredCallable<?, ?>... callables);
	*/

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		DeferredCallable<? extends A, ?> callableA,
		DeferredCallable<? extends B, ?> callableB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		DeferredCallable<? extends A, ?> callableA,
		DeferredCallable<? extends B, ?> callableB,
		DeferredCallable<? extends C, ?> callableC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		DeferredCallable<? extends A, ?> callableA,
		DeferredCallable<? extends B, ?> callableB,
		DeferredCallable<? extends C, ?> callableC,
		DeferredCallable<? extends D, ?> callableD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredCallable<? extends A, ?> callableA,
		DeferredCallable<? extends B, ?> callableB,
		DeferredCallable<? extends C, ?> callableC,
		DeferredCallable<? extends D, ?> callableD,
		DeferredCallable<? extends E, ?> callableE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredCallable<? extends A, ?> callableA,
		DeferredCallable<? extends B, ?> callableB,
		DeferredCallable<? extends C, ?> callableC,
		DeferredCallable<? extends D, ?> callableD,
		DeferredCallable<? extends E, ?> callableE,
		DeferredCallable<?, ?> callable,
		DeferredCallable<?, ?>... callables);
	/**
	 * May or may not submit {@link DeferredFutureTask} for execution. See
	 * implementation documentation.
	 * 
	 * @param tasks
	 * @return {@link #when(Promise...)}
	 */
	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			DeferredFutureTask<?, ?>... tasks);
	*/

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		DeferredFutureTask<? extends A, ?> taskA,
		DeferredFutureTask<? extends B, ?> taskB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		DeferredFutureTask<? extends A, ?> taskA,
		DeferredFutureTask<? extends B, ?> taskB,
		DeferredFutureTask<? extends C, ?> taskC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		DeferredFutureTask<? extends A, ?> taskA,
		DeferredFutureTask<? extends B, ?> taskB,
		DeferredFutureTask<? extends C, ?> taskC,
		DeferredFutureTask<? extends D, ?> taskD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredFutureTask<? extends A, ?> taskA,
		DeferredFutureTask<? extends B, ?> taskB,
		DeferredFutureTask<? extends C, ?> taskC,
		DeferredFutureTask<? extends D, ?> taskD,
		DeferredFutureTask<? extends E, ?> taskE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		DeferredFutureTask<? extends A, ?> taskA,
		DeferredFutureTask<? extends B, ?> taskB,
		DeferredFutureTask<? extends C, ?> taskC,
		DeferredFutureTask<? extends D, ?> taskD,
		DeferredFutureTask<? extends E, ?> taskE,
		DeferredFutureTask<?, ?> task,
		DeferredFutureTask<?, ?>... tasks);

	/*
	public abstract Promise<MultipleResults, OneReject<R>, MasterProgress> when(
			Future<?> ... futures);
	*/		

	<R,A,B> Promise<MultipleResults2<A,B>, OneReject<R>, MasterProgress> when(
		Future<? extends A> futureA,
		Future<? extends B> futureB);

	<R,A,B,C> Promise<MultipleResults3<A,B,C>, OneReject<R>, MasterProgress> when(
		Future<? extends A> futureA,
		Future<? extends B> futureB,
		Future<? extends C> futureC);

	<R,A,B,C,D> Promise<MultipleResults4<A,B,C,D>, OneReject<R>, MasterProgress> when(
		Future<? extends A> futureA,
		Future<? extends B> futureB,
		Future<? extends C> futureC,
		Future<? extends D> futureD);

	<R,A,B,C,D,E> Promise<MultipleResults5<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Future<? extends A> futureA,
		Future<? extends B> futureB,
		Future<? extends C> futureC,
		Future<? extends D> futureD,
		Future<? extends E> futureE);

	<R,A,B,C,D,E> Promise<MultipleResultsN<A,B,C,D,E>, OneReject<R>, MasterProgress> when(
		Future<? extends A> futureA,
		Future<? extends B> futureB,
		Future<? extends C> futureC,
		Future<? extends D> futureD,
		Future<? extends E> futureE,
		Future<?> future,
		Future<?>... futures);
}
