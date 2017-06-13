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
package org.jdeferred.impl;

import org.jdeferred.DeferredCallable;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredManager;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.Promise;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.MultipleResults2;
import org.jdeferred.multiple.MultipleResults3;
import org.jdeferred.multiple.MultipleResults4;
import org.jdeferred.multiple.MultipleResults5;
import org.jdeferred.multiple.MultipleResultsN;
import org.jdeferred.multiple.OneReject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractDeferredManager implements DeferredManager {
    final protected Logger log = LoggerFactory.getLogger(AbstractDeferredManager.class);

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
     *
     * @return
     */
    public abstract boolean isAutoSubmit();

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(Promise<? extends A, ?, ?> promiseA, Promise<? extends B, ?, ?> promiseB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(Promise<? extends A, ?, ?> promiseA, Promise<? extends B, ?, ?> promiseB, Promise<? extends C, ?, ?> promiseC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(Promise<? extends A, ?, ?> promiseA, Promise<? extends B, ?, ?> promiseB, Promise<? extends C, ?, ?> promiseC, Promise<? extends D, ?, ?> promiseD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Promise<? extends A, ?, ?> promiseA, Promise<? extends B, ?, ?> promiseB, Promise<? extends C, ?, ?> promiseC, Promise<? extends D, ?, ?> promiseD, Promise<? extends E, ?, ?> promiseE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Promise<? extends A, ?, ?> promiseA, Promise<? extends B, ?, ?> promiseB, Promise<? extends C, ?, ?> promiseC, Promise<? extends D, ?, ?> promiseD, Promise<? extends E, ?, ?> promiseE, Promise<?, ?, ?> promise, Promise<?, ?, ?>[] promises) {
        return null;
    }

    @Override
    public <R> Promise<MultipleResults, OneReject<R>, MasterProgress> when(Runnable... runnables) {
        assertNotEmpty(runnables);

        Promise[] promises = new Promise[runnables.length];

        for (int i = 0; i < runnables.length; i++) {
            if (runnables[i] instanceof DeferredRunnable) {
                promises[i] = when((DeferredRunnable) runnables[i]);
            } else {
                promises[i] = when(runnables[i]);
            }
        }

        switch (promises.length) {
            case 2: return when(promises[0], promises[1]);
            case 3: return when(promises[0], promises[1], promises[2]);
            case 4: return when(promises[0], promises[1], promises[2], promises[3]);
            case 5: return when(promises[0], promises[1], promises[2], promises[3], promises[4]);
            case 6: return when(promises[0], promises[1], promises[2], promises[3], promises[4], promises[5]);
            default:
                Promise[] promiseN = new Promise[promises.length - 6];
                System.arraycopy(promises, 6, promiseN, 0, promiseN.length);
                return when(promises[0], promises[1], promises[2], promises[3], promises[4], promises[5], promiseN);
        }
    }

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(Callable<? extends A> callableA, Callable<? extends B> callableB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(Callable<? extends A> callableA, Callable<? extends B> callableB, Callable<? extends C> callableC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(Callable<? extends A> callableA, Callable<? extends B> callableB, Callable<? extends C> callableC, Callable<? extends D> callableD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Callable<? extends A> callableA, Callable<? extends B> callableB, Callable<? extends C> callableC, Callable<? extends D> callableD, Callable<? extends E> callableE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Callable<? extends A> callableA, Callable<? extends B> callableB, Callable<? extends C> callableC, Callable<? extends D> callableD, Callable<? extends E> callableE, Callable<?> callable, Callable<?>[] callables) {
        return null;
    }

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(DeferredRunnable<? extends A> runnableA, DeferredRunnable<? extends B> runnableB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(DeferredRunnable<? extends A> runnableA, DeferredRunnable<? extends B> runnableB, DeferredRunnable<? extends C> runnableC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(DeferredRunnable<? extends A> runnableA, DeferredRunnable<? extends B> runnableB, DeferredRunnable<? extends C> runnableC, DeferredRunnable<? extends D> runnableD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredRunnable<? extends A> runnableA, DeferredRunnable<? extends B> runnableB, DeferredRunnable<? extends C> runnableC, DeferredRunnable<? extends D> runnableD, DeferredRunnable<? extends E> runnableE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredRunnable<? extends A> runnableA, DeferredRunnable<? extends B> runnableB, DeferredRunnable<? extends C> runnableC, DeferredRunnable<? extends D> runnableD, DeferredRunnable<? extends E> runnableE, DeferredRunnable<?> runnable, DeferredRunnable<?>[] runnables) {
        return null;
    }

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(DeferredCallable<? extends A, ?> callableA, DeferredCallable<? extends B, ?> callableB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(DeferredCallable<? extends A, ?> callableA, DeferredCallable<? extends B, ?> callableB, DeferredCallable<? extends C, ?> callableC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(DeferredCallable<? extends A, ?> callableA, DeferredCallable<? extends B, ?> callableB, DeferredCallable<? extends C, ?> callableC, DeferredCallable<? extends D, ?> callableD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredCallable<? extends A, ?> callableA, DeferredCallable<? extends B, ?> callableB, DeferredCallable<? extends C, ?> callableC, DeferredCallable<? extends D, ?> callableD, DeferredCallable<? extends E, ?> callableE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredCallable<? extends A, ?> callableA, DeferredCallable<? extends B, ?> callableB, DeferredCallable<? extends C, ?> callableC, DeferredCallable<? extends D, ?> callableD, DeferredCallable<? extends E, ?> callableE, DeferredCallable<?, ?> callable, DeferredCallable<?, ?>[] callables) {
        return null;
    }

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(DeferredFutureTask<? extends A, ?> taskA, DeferredFutureTask<? extends B, ?> taskB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(DeferredFutureTask<? extends A, ?> taskA, DeferredFutureTask<? extends B, ?> taskB, DeferredFutureTask<? extends C, ?> taskC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(DeferredFutureTask<? extends A, ?> taskA, DeferredFutureTask<? extends B, ?> taskB, DeferredFutureTask<? extends C, ?> taskC, DeferredFutureTask<? extends D, ?> taskD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredFutureTask<? extends A, ?> taskA, DeferredFutureTask<? extends B, ?> taskB, DeferredFutureTask<? extends C, ?> taskC, DeferredFutureTask<? extends D, ?> taskD, DeferredFutureTask<? extends E, ?> taskE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(DeferredFutureTask<? extends A, ?> taskA, DeferredFutureTask<? extends B, ?> taskB, DeferredFutureTask<? extends C, ?> taskC, DeferredFutureTask<? extends D, ?> taskD, DeferredFutureTask<? extends E, ?> taskE, DeferredFutureTask<?, ?> task, DeferredFutureTask<?, ?>[] tasks) {
        return null;
    }

    @Override
    public <R, A, B> Promise<MultipleResults2<A, B>, OneReject<R>, MasterProgress> when(Future<? extends A> futureA, Future<? extends B> futureB) {
        return null;
    }

    @Override
    public <R, A, B, C> Promise<MultipleResults3<A, B, C>, OneReject<R>, MasterProgress> when(Future<? extends A> futureA, Future<? extends B> futureB, Future<? extends C> futureC) {
        return null;
    }

    @Override
    public <R, A, B, C, D> Promise<MultipleResults4<A, B, C, D>, OneReject<R>, MasterProgress> when(Future<? extends A> futureA, Future<? extends B> futureB, Future<? extends C> futureC, Future<? extends D> futureD) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResults5<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Future<? extends A> futureA, Future<? extends B> futureB, Future<? extends C> futureC, Future<? extends D> futureD, Future<? extends E> futureE) {
        return null;
    }

    @Override
    public <R, A, B, C, D, E> Promise<MultipleResultsN<A, B, C, D, E>, OneReject<R>, MasterProgress> when(Future<? extends A> futureA, Future<? extends B> futureB, Future<? extends C> futureC, Future<? extends D> futureD, Future<? extends E> futureE, Future<?> future, Future<?>[] futures) {
        return null;
    }

/*
	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(Runnable... runnables) {
		assertNotEmpty(runnables);
		
		Promise[] promises = new Promise[runnables.length];

		for (int i = 0; i < runnables.length; i++) {
			if (runnables[i] instanceof DeferredRunnable)
				promises[i] = when((DeferredRunnable) runnables[i]);
			else
				promises[i] = when(runnables[i]);
		}

		return when(promises);
	}

	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(Callable<?>... callables) {
		assertNotEmpty(callables);

		Promise[] promises = new Promise[callables.length]; 

		for (int i = 0; i < callables.length; i++) {
			if (callables[i] instanceof DeferredCallable)
				promises[i] = when((DeferredCallable) callables[i]);
			else
				promises[i] = when(callables[i]);
		}

		return when(promises);
	}
	
	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredRunnable<?>... runnables) {
		assertNotEmpty(runnables);
		
		Promise[] promises = new Promise[runnables.length];

		for (int i = 0; i < runnables.length; i++) {
			promises[i] = when(runnables[i]);
		}

		return when(promises);
	}

	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredCallable<?, ?>... callables) {
		assertNotEmpty(callables);

		Promise[] promises = new Promise[callables.length]; 

		for (int i = 0; i < callables.length; i++) {
			promises[i] = when(callables[i]);
		}

		return when(promises);
	}

	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(DeferredFutureTask<?, ?>... tasks) {
		assertNotEmpty(tasks);

		Promise[] promises = new Promise[tasks.length];

		for (int i = 0; i < tasks.length; i++) {
			promises[i] = when(tasks[i]);
		}
		return when(promises);
	}

	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(Future<?> ... futures) {
		assertNotEmpty(futures);

		Promise[] promises = new Promise[futures.length];

		for (int i = 0; i < futures.length; i++) {
			promises[i] = when(futures[i]);
		}
		return when(promises);
	}

	@Override
	public Promise<MultipleResults, OneReject, MasterProgress> when(Promise... promises) {
		assertNotEmpty(promises);
		return new MasterDeferredObject(promises).promise();
	}
	*/

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

    /**
     * This method is delegated by at least the following methods
     * <ul>
     * <li>{@link #when(Callable)}</li>
     * <li>{@link #when(Callable...)}</li>
     * <li>{@link #when(Runnable)}</li>
     * <li>{@link #when(Runnable..)}</li>
     * <li>{@link #when(java.util.concurrent.Future)}</li>
     * <li>{@link #when(java.util.concurrent.Future...)}</li>
     * <li>{@link #when(org.jdeferred.DeferredRunnable...)}</li>
     * <li>{@link #when(org.jdeferred.DeferredRunnable)}</li>
     * <li>{@link #when(org.jdeferred.DeferredCallable...)}</li>
     * <li>{@link #when(org.jdeferred.DeferredCallable)}</li>
     * <li>{@link #when(DeferredFutureTask...)}</li>
     * </ul>
     */
    @Override
    public <D, P> Promise<D, Throwable, P> when(
        DeferredFutureTask<D, P> task) {
        if (task.getStartPolicy() == StartPolicy.AUTO
            || (task.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) { submit(task); }

        return task.promise();
    }

    @Override
    public <D> Promise<D, Throwable, Void> when(final Future<D> future) {
        // make sure the task is automatically started

        return when(new DeferredCallable<D, Void>(StartPolicy.AUTO) {
            @Override
            public D call() throws Exception {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    throw e;
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof Exception) { throw (Exception) e.getCause(); } else { throw e; }
                }
            }
        });
    }

    protected void assertNotEmpty(Object[] objects) {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException(
                "Arguments is null or its length is empty");
        }
    }
}
