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
package org.jdeferred.impl;

import java.util.concurrent.Callable;

import org.jdeferred.DeferredCallable;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredManager;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.Promise;
import org.jdeferred.multiple.CombinedPromise;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractDeferredManager implements DeferredManager {
	protected abstract void submit(Runnable runnable);
	protected abstract void submit(Callable callable);
	
	/**
	 * Should {@link Runnable} or {@link Callable} be submitted for execution automatically
	 * when any of the following are called
	 * <ul>
	 * <li>{@link #when(Runnable...)}</li>
	 * <li>{@link #when(Callable...)}</li>
	 * <li>{@link #when(DeferredFutureTask...))}</li>
	 * <li>{@link #when(DeferredCallable)}</li>
	 * <li>{@link #when(DeferredRunnable)}</li>
	 * <li>{@link #when(DeferredFutureTask))}</li>
	 * </ul>
	 * @return
	 */
	public abstract boolean isAutoSubmit();

	@Override
	public CombinedPromise when(Runnable... runnables) {
		assertNotEmpty(runnables);
		DeferredFutureTask<Void, Void>[] tasks = new DeferredFutureTask[runnables.length];

		for (int i = 0; i < runnables.length; i++) {
			if (runnables[i] instanceof DeferredRunnable)
				tasks[i] = new DeferredFutureTask((DeferredRunnable) runnables[i]);
			else
				tasks[i] = new DeferredFutureTask(runnables[i]);
		}

		return when(tasks);
	}

	@Override
	public CombinedPromise when(Callable<?>... callables) {
		assertNotEmpty(callables);

		DeferredFutureTask<?, ?>[] tasks = new DeferredFutureTask<?, ?>[callables.length];

		for (int i = 0; i < callables.length; i++) {
			if (callables[i] instanceof DeferredCallable)
				tasks[i] = new DeferredFutureTask((DeferredCallable) callables[i]);
			else
				tasks[i] = new DeferredFutureTask(callables[i]);
		}

		return when(tasks);
	}
	
	@Override
	public CombinedPromise when(DeferredRunnable<?>... runnables) {
		return when((Runnable[]) runnables);
	}

	@Override
	public CombinedPromise when(DeferredCallable<?, ?>... callables) {
		return when((Callable[]) callables);
	}

	@Override
	public CombinedPromise when(DeferredFutureTask<?, ?>... tasks) {
		assertNotEmpty(tasks);

		Promise[] promises = new Promise[tasks.length];

		for (int i = 0; i < tasks.length; i++) {
			if (isAutoSubmit()) submit(tasks[i]);
			promises[i] = tasks[i].promise();
		}
		return when(promises);
	}

	@Override
	public CombinedPromise when(Promise... promises) {
		assertNotEmpty(promises);
		return new CombinedPromise(promises);
	}

	@Override
	public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
		return promise;
	}

	@Override
	public <P> Promise<Void, Throwable, P> when(DeferredRunnable<P> runnable) {
		return when(new DeferredFutureTask<Void, P>(runnable));
	}

	@Override
	public <D, P> Promise<D, Throwable, P> when(DeferredCallable<D, P> runnable) {
		return when(new DeferredFutureTask<D, P>(runnable));
	}
	
	@Override
	public Promise<Void, Throwable, Void> when(Runnable runnable) {
		return when(new DeferredFutureTask<Void, Void>(runnable));
	}

	@Override
	public <D> Promise<D, Throwable, Void> when(Callable<D> callable) {
		return when(new DeferredFutureTask<D, Void>(callable));
	}

	@Override
	public <D, P> Promise<D, Throwable, P> when(
			DeferredFutureTask<D, P> task) {
		if (isAutoSubmit()) submit(task);
		return task.promise();
	}
	
	protected void assertNotEmpty(Object[] objects) {
		if (objects == null || objects.length == 0)
			throw new IllegalArgumentException(
					"Arguments is null or its length is empty");
	}	
}
