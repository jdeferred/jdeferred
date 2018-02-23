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

import org.jdeferred2.DeferredManager.StartPolicy;
import org.jdeferred2.impl.DeferredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * FutureTask can wrap around {@link Callable} and {@link Runnable}.
 * In these two cases, a new {@link Deferred} object will be created.
 * This class will override {@link FutureTask#done} to trigger the
 * appropriate {@link Deferred} actions.
 * <p>
 * Note, type used for {@link Deferred#reject(Object)} is always {@link Throwable}.
 * <p>
 * When the task is completed successfully, {@link Deferred#resolve(Object)} will be called.
 * When a task is canceled, {@link Deferred#reject(Object)} will be called with an instance of {@link CancellationException}
 * If any Exception occurred, {@link Deferred#reject(Object)} will be called with the Exception instance.
 *
 * @param <D> Type used for {@link Deferred#resolve(Object)}
 * @param <P> Type used for {@link Deferred#notify(Object)}
 *
 * @author Ray Tsang
 */
public class DeferredFutureTask<D, P> extends FutureTask<D> {
	private static final Logger LOG = LoggerFactory.getLogger(DeferredFutureTask.class);

	protected final Deferred<D, Throwable, P> deferred;
	protected final StartPolicy startPolicy;
	private Object taskDelegate;
	private CancellationHandler cancellationHandler;

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task.
	 * The given task may implement the {@code CancellationHandler} interface.
	 *
	 * @param task the task to be executed. Must not be null.
	 */
	public DeferredFutureTask(Callable<D> task) {
		this(task, null);
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task.
	 * The given task may implement the {@code CancellationHandler} interface.
	 *
	 * @param task the task to be executed. Must not be null.
	 */
	public DeferredFutureTask(Runnable task) {
		this(task, null);
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task.
	 * The given task may implement the {@code CancellationHandler} interface.
	 *
	 * @param task the task to be executed. Must not be null.
	 */
	public DeferredFutureTask(DeferredCallable<D, P> task) {
		this(task, null);
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task.
	 * The given task may implement the {@code CancellationHandler} interface.
	 *
	 * @param task the task to be executed. Must not be null.
	 */
	public DeferredFutureTask(DeferredRunnable<P> task) {
		this(task, null);
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task and a explicit {@code CancellationHandler}
	 * The given {@code cancellationHandler} has precedence over the given task if the task implements the {@code CancellationHandler} interface.
	 *
	 * @param task                the task to be executed. Must not be null.
	 * @param cancellationHandler the {@code CancellationHandler} to invoke during onCancel. May be null.
	 *
	 * @since 2.0
	 */
	public DeferredFutureTask(Callable<D> task, CancellationHandler cancellationHandler) {
		super(task);
		this.taskDelegate = task;
		this.cancellationHandler = cancellationHandler;
		this.deferred = new DeferredObject<D, Throwable, P>();
		this.startPolicy = StartPolicy.DEFAULT;
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task and a explicit {@code CancellationHandler}
	 * The given {@code cancellationHandler} has precedence over the given task if the task implements the {@code CancellationHandler} interface.
	 *
	 * @param task                the task to be executed. Must not be null.
	 * @param cancellationHandler the {@code CancellationHandler} to invoke during onCancel. May be null.
	 *
	 * @since 2.0
	 */
	public DeferredFutureTask(Runnable task, CancellationHandler cancellationHandler) {
		super(task, null);
		this.taskDelegate = task;
		this.cancellationHandler = cancellationHandler;
		this.deferred = new DeferredObject<D, Throwable, P>();
		this.startPolicy = StartPolicy.DEFAULT;
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task and a explicit {@code CancellationHandler}
	 * The given {@code cancellationHandler} has precedence over the given task if the task implements the {@code CancellationHandler} interface.
	 *
	 * @param task                the task to be executed. Must not be null.
	 * @param cancellationHandler the {@code CancellationHandler} to invoke during onCancel. May be null.
	 *
	 * @since 2.0
	 */
	public DeferredFutureTask(DeferredCallable<D, P> task, CancellationHandler cancellationHandler) {
		super(task);
		this.taskDelegate = task;
		this.cancellationHandler = cancellationHandler;
		this.deferred = task.getDeferred();
		this.startPolicy = task.getStartPolicy();
	}

	/**
	 * Creates a new {@code DeferredFutureTask} with the given task and a explicit {@code CancellationHandler}
	 * The given {@code cancellationHandler} has precedence over the given task if the task implements the {@code CancellationHandler} interface.
	 *
	 * @param task                the task to be executed. Must not be null.
	 * @param cancellationHandler the {@code CancellationHandler} to invoke during onCancel. May be null.
	 *
	 * @since 2.0
	 */
	@SuppressWarnings("unchecked")
	public DeferredFutureTask(DeferredRunnable<P> task, CancellationHandler cancellationHandler) {
		super(task, null);
		this.taskDelegate = task;
		this.cancellationHandler = cancellationHandler;
		this.deferred = (Deferred<D, Throwable, P>) task.getDeferred();
		this.startPolicy = task.getStartPolicy();
	}

	public Promise<D, Throwable, P> promise() {
		return deferred.promise();
	}

	@Override
	protected void done() {
		if (isCancelled()) {
			deferred.reject(new CancellationException());
			cleanup();
			return;
		}

		try {
			deferred.resolve(get());
		} catch (InterruptedException e) {
			try {
				deferred.reject(causeOf(e));
			} finally {
				cleanup();
			}
		} catch (ExecutionException e) {
			try {
				deferred.reject(causeOf(e));
			} finally {
				cleanup();
			}
		} catch (Throwable t) {
			// TODO: forward to global ExceptionHandler
			LOG.warn("Unexpected error when resolving value", t);
		}
	}

	protected Throwable causeOf(Exception e) {
		return e.getCause() != null ? e.getCause() : e;
	}

	/**
	 * Performs resource cleanup upon interruption or cancellation of the underlying task.
	 * This method gives precedence to {@code cancellationHandler} it not null, otherwise
	 * it invokes the underlying task's {@code onCancel()} if it implements the {@code CancellationHandler} interface.
	 *
	 * @since 2.0
	 */
	protected void cleanup() {
		try {
			if (cancellationHandler != null) {
				cancellationHandler.onCancel();
			} else if (taskDelegate instanceof CancellationHandler) {
				((CancellationHandler) taskDelegate).onCancel();
			}
		} catch (Throwable t) {
			// TODO: forward to global ExceptionHandler
			LOG.warn("Unexpected error when cleaning up", t);
		}
	}

	public StartPolicy getStartPolicy() {
		return startPolicy;
	}
}
