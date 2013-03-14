/*
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
 */
package org.jdeferred;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.multiple.MasterDeferredObject;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
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
		 */
		MANAUL
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
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			Promise... promises);

	/**
	 * Wraps {@link Runnable} with {@link DeferredFutureTask}
	 * 
	 * @param runnables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			Runnable... runnables);

	/**
	 * Wraps {@link Callable} with {@link DeferredFutureTask}
	 * 
	 * @param callables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			Callable<?>... callables);

	/**
	 * Wraps {@link DeferredRunnable} with {@link DeferredFutureTask}
	 * 
	 * @param runnables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			DeferredRunnable<?>... runnables);

	/**
	 * Wraps {@link DeferredCallable} with {@link DeferredFutureTask}
	 * 
	 * @param callables
	 * @return {@link #when(DeferredFutureTask...)}
	 */
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			DeferredCallable<?, ?>... callables);

	/**
	 * May or may not submit {@link DeferredFutureTask} for execution. See
	 * implementation documentation.
	 * 
	 * @param tasks
	 * @return {@link #when(Promise...)}
	 */
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			DeferredFutureTask<?, ?>... tasks);
	
	public abstract Promise<MultipleResults, OneReject, MasterProgress> when(
			Future<?> ... futures);

}
