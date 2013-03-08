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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jdeferred.DeferredManager.StartPolicy;
import org.jdeferred.impl.DeferredObject;

/**
 * FutureTask can wrap around {@link Callable} and {@link Runnable}.
 * In these two cases, a new {@link Deferred} object will be created.
 * This class will override {@link FutureTask#done} to trigger the 
 * appropriate {@link Deferred} actions.
 * 
 * Note, type used for {@link Deferred#reject(Object)} is always {@link Throwable}.
 * 
 * When the task is completed successfully, {@link Deferred#resolve(Object)} will be called.
 * When a task is canceled, {@link Deferred#reject(Object)} will be called with an instance of {@link CancellationException}
 * If any Exception occured, {@link Deferred#reject(Object)} will be called with the Exception instance.
 * 
 * @author Ray Tsang
 *
 * @param <D> Type used for {@link Deferred#resolve(Object)}
 * @param <P> Type used for {@link Deferred#notify(Object)}
 */
public class DeferredFutureTask<D, P> extends FutureTask<D> {
	protected final Deferred<D, Throwable, P> deferred;
	protected final StartPolicy startPolicy;
	
	public DeferredFutureTask(Callable<D> callable) {
		super(callable);
		this.deferred = new DeferredObject<D, Throwable, P>();
		this.startPolicy = StartPolicy.DEFAULT;
	}
	
	public DeferredFutureTask(Runnable runnable) {
		super(runnable, null);
		this.deferred = new DeferredObject<D, Throwable, P>();
		this.startPolicy = StartPolicy.DEFAULT;
	}
	
	public DeferredFutureTask(DeferredCallable<D, P> callable) {
		super(callable);
		this.deferred = callable.getDeferred();
		this.startPolicy = callable.getStartPolicy();
	}
	
	@SuppressWarnings("unchecked")
	public DeferredFutureTask(DeferredRunnable<P> runnable) {
		super(runnable, null);
		this.deferred = (Deferred<D, Throwable, P>) runnable.getDeferred();
		this.startPolicy = runnable.getStartPolicy();
	}
	
	public Promise<D, Throwable, P> promise() {
		return deferred.promise();
	}
	
	@Override
	protected void done() {
		try {
			if (isCancelled()) {
				deferred.reject(new CancellationException());
			}
			D result = get();
			deferred.resolve(result);
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
			deferred.reject(e.getCause());
		}
	}

	public StartPolicy getStartPolicy() {
		return startPolicy;
	}
}
