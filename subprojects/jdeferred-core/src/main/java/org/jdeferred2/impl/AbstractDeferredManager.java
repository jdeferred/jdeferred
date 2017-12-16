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
package org.jdeferred2.impl;

import org.jdeferred2.DeferredCallable;
import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DeferredManager;
import org.jdeferred2.DeferredRunnable;
import org.jdeferred2.Promise;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractDeferredManager implements DeferredManager {
	protected static final String PROMISE_V1 = "promiseV1";
	protected static final String PROMISE_V2 = "promiseV2";
	protected static final String PROMISE_V3 = "promiseV3";
	protected static final String PROMISE_V4 = "promiseV4";
	protected static final String PROMISE_V5 = "promiseV5";

	protected static final String CALLABLE_V1 = "callableV1";
	protected static final String CALLABLE_V2 = "callableV2";
	protected static final String CALLABLE_V3 = "callableV3";
	protected static final String CALLABLE_V4 = "callableV4";
	protected static final String CALLABLE_V5 = "callableV5";

	protected static final String RUNNABLE_V1 = "runnableV1";
	protected static final String RUNNABLE_V2 = "runnableV2";
	protected static final String RUNNABLE_V3 = "runnableV3";
	protected static final String RUNNABLE_V4 = "runnableV4";
	protected static final String RUNNABLE_V5 = "runnableV5";

	protected static final String TASK_V1 = "taskV1";
	protected static final String TASK_V2 = "taskV2";
	protected static final String TASK_V3 = "taskV3";
	protected static final String TASK_V4 = "taskV4";
	protected static final String TASK_V5 = "taskV5";

	protected static final String FUTURE_V1 = "futureV1";
	protected static final String FUTURE_V2 = "futureV2";
	protected static final String FUTURE_V3 = "futureV3";
	protected static final String FUTURE_V4 = "futureV4";
	protected static final String FUTURE_V5 = "futureV5";

	final protected Logger log = LoggerFactory.getLogger(AbstractDeferredManager.class);

	protected abstract void submit(Runnable runnable);

	protected abstract void submit(Callable callable);

	/**
	 * Should {@link Runnable} or {@link Callable} be submitted for execution automatically
	 * when any of the {@code when()} method variants is invoked.
	 *
	 * @return
	 */
	public abstract boolean isAutoSubmit();

	@Override
	public <F, V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<F>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2) {
		assertNotNull(promiseV1, PROMISE_V1);
		assertNotNull(promiseV2, PROMISE_V2);
		return new MasterDeferredObject2(promiseV1, promiseV2);
	}

	@Override
	public <F, V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<F>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3) {
		assertNotNull(promiseV1, PROMISE_V1);
		assertNotNull(promiseV2, PROMISE_V2);
		assertNotNull(promiseV3, PROMISE_V3);
		return new MasterDeferredObject3(promiseV1, promiseV2, promiseV3);
	}

	@Override
	public <F, V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<F>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4) {
		assertNotNull(promiseV1, PROMISE_V1);
		assertNotNull(promiseV2, PROMISE_V2);
		assertNotNull(promiseV3, PROMISE_V3);
		assertNotNull(promiseV4, PROMISE_V4);
		return new MasterDeferredObject4(promiseV1, promiseV2, promiseV3, promiseV4);
	}

	@Override
	public <F, V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5) {
		assertNotNull(promiseV1, PROMISE_V1);
		assertNotNull(promiseV2, PROMISE_V2);
		assertNotNull(promiseV3, PROMISE_V3);
		assertNotNull(promiseV4, PROMISE_V4);
		assertNotNull(promiseV5, PROMISE_V5);
		return new MasterDeferredObject5(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5);
	}

	@Override
	public <F, V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<F>, MasterProgress> when(Promise<V1, ?, ?> promiseV1, Promise<V2, ?, ?> promiseV2, Promise<V3, ?, ?> promiseV3, Promise<V4, ?, ?> promiseV4, Promise<V5, ?, ?> promiseV5, Promise<?, ?, ?> promise6, Promise<?, ?, ?>... promises) {
		assertNotNull(promiseV1, PROMISE_V1);
		assertNotNull(promiseV2, PROMISE_V2);
		assertNotNull(promiseV3, PROMISE_V3);
		assertNotNull(promiseV4, PROMISE_V4);
		assertNotNull(promiseV5, PROMISE_V5);
		assertNotNull(promise6, "promise6");

		Promise[] promiseN = new Promise[promises.length - 5];
		System.arraycopy(promises, 5, promiseN, 0, promiseN.length);
		return new MasterDeferredObjectN(promiseV1, promiseV2, promiseV3, promiseV4, promiseV5, promise6, promiseN);
	}

	@Override
	public Promise<MultipleResults, OneReject<Throwable>, MasterProgress> when(Runnable runnable1, Runnable runnable2, Runnable... runnables) {
		assertNotNull(runnable1, "runnable1");
		assertNotNull(runnable2, "runnable2");
		Promise[] promises = new Promise[runnables.length + 2];
		promises[0] = when(runnable1);
		promises[1] = when(runnable2);

		for (int i = 0; i < runnables.length; i++) {
			if (runnables[i] instanceof DeferredRunnable) {
				promises[i + 2] = when((DeferredRunnable<?>) runnables[i]);
			} else {
				promises[i + 2] = when(runnables[i]);
			}
		}

		switch (promises.length) {
			case 2: return when(promises[0], promises[1]);
			case 3: return when(promises[0], promises[1], promises[2]);
			case 4: return when(promises[0], promises[1], promises[2], promises[3]);
			case 5: return when(promises[0], promises[1], promises[2], promises[3], promises[4]);
			default:
				Promise[] promiseN = new Promise[promises.length - 5];
				System.arraycopy(promises, 5, promiseN, 0, promiseN.length);
				return new MasterDeferredObjectN(promises[0], promises[1], promises[2], promises[3], promises[4], promises[5], promiseN);
		}
	}

	@Override
	public <V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(Callable<V1> callableV1, Callable<V2> callableV2) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		return new MasterDeferredObject2(when(callableV1), when(callableV2));
	}

	@Override
	public <V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(Callable<V1> callableV1, Callable<V2> callableV2, Callable<V3> callableV3) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		return new MasterDeferredObject3(when(callableV1), when(callableV2), when(callableV3));
	}

	@Override
	public <V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(Callable<V1> callableV1, Callable<V2> callableV2, Callable<V3> callableV3, Callable<V4> callableV4) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		return new MasterDeferredObject4(when(callableV1), when(callableV2), when(callableV3), when(callableV4));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(Callable<V1> callableV1, Callable<V2> callableV2, Callable<V3> callableV3, Callable<V4> callableV4, Callable<V5> callableV5) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		assertNotNull(callableV5, CALLABLE_V5);
		return new MasterDeferredObject5(when(callableV1), when(callableV2), when(callableV3), when(callableV4), when(callableV5));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(Callable<V1> callableV1, Callable<V2> callableV2, Callable<V3> callableV3, Callable<V4> callableV4, Callable<V5> callableV5, Callable<?> callable6, Callable<?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		assertNotNull(callableV5, CALLABLE_V5);
		assertNotNull(callable6, "callable6");

		Promise<V1, Throwable, ?> promise1 = when(callableV1);
		Promise<V2, Throwable, ?> promise2 = when(callableV2);
		Promise<V3, Throwable, ?> promise3 = when(callableV3);
		Promise<V4, Throwable, ?> promise4 = when(callableV4);
		Promise<V5, Throwable, ?> promise5 = when(callableV5);

		Promise[] promiseN = new Promise[callables.length];
		for (int i = 0; i < callables.length; i++) {
			if (callables[i] instanceof DeferredCallable) {
				promiseN[i] = when((DeferredCallable) callables[i]);
			} else {
				promiseN[i] = when(callables[i]);
			}
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, when(callable6), promiseN);
	}

	@Override
	public <P1, P2> Promise<MultipleResults2<Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2) {
		assertNotNull(runnableP1, RUNNABLE_V1);
		assertNotNull(runnableP2, RUNNABLE_V2);
		return new MasterDeferredObject2(when(runnableP1), when(runnableP2));
	}

	@Override
	public <P1, P2, P3> Promise<MultipleResults3<Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3) {
		assertNotNull(runnableP1, RUNNABLE_V1);
		assertNotNull(runnableP2, RUNNABLE_V2);
		assertNotNull(runnableP3, RUNNABLE_V3);
		return new MasterDeferredObject3(when(runnableP1), when(runnableP2), when(runnableP3));
	}

	@Override
	public <P1, P2, P3, P4> Promise<MultipleResults4<Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4) {
		assertNotNull(runnableP1, RUNNABLE_V1);
		assertNotNull(runnableP2, RUNNABLE_V2);
		assertNotNull(runnableP3, RUNNABLE_V3);
		assertNotNull(runnableP4, RUNNABLE_V4);
		return new MasterDeferredObject4(when(runnableP1), when(runnableP2), when(runnableP3), when(runnableP4));
	}

	@Override
	public <P1, P2, P3, P4, P5> Promise<MultipleResults5<Void, Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4,
		DeferredRunnable<P5> runnableP5) {
		assertNotNull(runnableP1, RUNNABLE_V1);
		assertNotNull(runnableP2, RUNNABLE_V2);
		assertNotNull(runnableP3, RUNNABLE_V3);
		assertNotNull(runnableP4, RUNNABLE_V4);
		assertNotNull(runnableP5, RUNNABLE_V5);
		return new MasterDeferredObject5(when(runnableP1), when(runnableP2), when(runnableP3), when(runnableP4), when(runnableP5));
	}

	@Override
	public <P1, P2, P3, P4, P5> Promise<MultipleResultsN<Void, Void, Void, Void, Void>, OneReject<Throwable>, MasterProgress> when(
		DeferredRunnable<P1> runnableP1,
		DeferredRunnable<P2> runnableP2,
		DeferredRunnable<P3> runnableP3,
		DeferredRunnable<P4> runnableP4,
		DeferredRunnable<P5> runnableP5,
		DeferredRunnable<?> runnable6,
		DeferredRunnable<?>... runnables) {
		assertNotNull(runnableP1, RUNNABLE_V1);
		assertNotNull(runnableP2, RUNNABLE_V2);
		assertNotNull(runnableP3, RUNNABLE_V3);
		assertNotNull(runnableP4, RUNNABLE_V4);
		assertNotNull(runnableP5, RUNNABLE_V5);
		assertNotNull(runnable6, "runnable6");

		Promise<Void, Throwable, P1> promise1 = when(runnableP1);
		Promise<Void, Throwable, P2> promise2 = when(runnableP2);
		Promise<Void, Throwable, P3> promise3 = when(runnableP3);
		Promise<Void, Throwable, P4> promise4 = when(runnableP4);
		Promise<Void, Throwable, P5> promise5 = when(runnableP5);
		Promise<?, Throwable, ?> promise6 = when(runnable6);

		Promise[] promiseN = new Promise[runnables.length];
		for (int i = 0; i < runnables.length; i++) {
			promiseN[i] = when(runnables[i]);
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, promise6, promiseN);
	}

	@Override
	public <V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(DeferredCallable<V1, ?> callableV1, DeferredCallable<V2, ?> callableV2) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		return new MasterDeferredObject2(when(callableV1), when(callableV2));
	}

	@Override
	public <V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(DeferredCallable<V1, ?> callableV1, DeferredCallable<V2, ?> callableV2, DeferredCallable<V3, ?> callableV3) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		return new MasterDeferredObject3(when(callableV1), when(callableV2), when(callableV3));
	}

	@Override
	public <V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(DeferredCallable<V1, ?> callableV1, DeferredCallable<V2, ?> callableV2, DeferredCallable<V3, ?> callableV3, DeferredCallable<V4, ?> callableV4) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		return new MasterDeferredObject4(when(callableV1), when(callableV2), when(callableV3), when(callableV4));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(DeferredCallable<V1, ?> callableV1, DeferredCallable<V2, ?> callableV2, DeferredCallable<V3, ?> callableV3, DeferredCallable<V4, ?> callableV4, DeferredCallable<V5, ?> callableV5) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		assertNotNull(callableV5, CALLABLE_V5);
		return new MasterDeferredObject5(when(callableV1), when(callableV2), when(callableV3), when(callableV4), when(callableV5));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(DeferredCallable<V1, ?> callableV1, DeferredCallable<V2, ?> callableV2, DeferredCallable<V3, ?> callableV3, DeferredCallable<V4, ?> callableV4, DeferredCallable<V5, ?> callableV5, DeferredCallable<?, ?> callable6, DeferredCallable<?, ?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);
		assertNotNull(callableV3, CALLABLE_V3);
		assertNotNull(callableV4, CALLABLE_V4);
		assertNotNull(callableV5, CALLABLE_V5);
		assertNotNull(callable6, "callable6");

		Promise<V1, Throwable, ?> promise1 = when(callableV1);
		Promise<V2, Throwable, ?> promise2 = when(callableV2);
		Promise<V3, Throwable, ?> promise3 = when(callableV3);
		Promise<V4, Throwable, ?> promise4 = when(callableV4);
		Promise<V5, Throwable, ?> promise5 = when(callableV5);
		Promise<?, Throwable, ?> promise6 = when(callable6);

		Promise[] promiseN = new Promise[callables.length];
		for (int i = 0; i < callables.length; i++) {
			promiseN[i] = when(callables[i]);
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, promise6, promiseN);
	}

	@Override
	public <V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(DeferredFutureTask<V1, ?> taskV1, DeferredFutureTask<V2, ?> taskV2) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		return new MasterDeferredObject2(when(taskV1), when(taskV2));
	}

	@Override
	public <V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(DeferredFutureTask<V1, ?> taskV1, DeferredFutureTask<V2, ?> taskV2, DeferredFutureTask<V3, ?> taskV3) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		return new MasterDeferredObject3(when(taskV1), when(taskV2), when(taskV3));
	}

	@Override
	public <V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(DeferredFutureTask<V1, ?> taskV1, DeferredFutureTask<V2, ?> taskV2, DeferredFutureTask<V3, ?> taskV3, DeferredFutureTask<V4, ?> taskV4) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		return new MasterDeferredObject4(when(taskV1), when(taskV2), when(taskV3), when(taskV4));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(DeferredFutureTask<V1, ?> taskV1, DeferredFutureTask<V2, ?> taskV2, DeferredFutureTask<V3, ?> taskV3, DeferredFutureTask<V4, ?> taskV4, DeferredFutureTask<V5, ?> taskV5) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);
		assertNotNull(taskV3, TASK_V3);
		assertNotNull(taskV4, TASK_V4);
		assertNotNull(taskV5, TASK_V5);
		return new MasterDeferredObject5(when(taskV1), when(taskV2), when(taskV3), when(taskV4), when(taskV5));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(DeferredFutureTask<V1, ?> taskV1, DeferredFutureTask<V2, ?> taskV2, DeferredFutureTask<V3, ?> taskV3, DeferredFutureTask<V4, ?> taskV4, DeferredFutureTask<V5, ?> taskV5, DeferredFutureTask<?, ?> task6, DeferredFutureTask<?, ?>... tasks) {
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

	@Override
	public <V1, V2> Promise<MultipleResults2<V1, V2>, OneReject<Throwable>, MasterProgress> when(Future<V1> futureV1, Future<V2> futureV2) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);
		return new MasterDeferredObject2(when(futureV1), when(futureV2));
	}

	@Override
	public <V1, V2, V3> Promise<MultipleResults3<V1, V2, V3>, OneReject<Throwable>, MasterProgress> when(Future<V1> futureV1, Future<V2> futureV2, Future<V3> futureV3) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);
		assertNotNull(futureV3, FUTURE_V3);
		return new MasterDeferredObject3(when(futureV1), when(futureV2), when(futureV3));
	}

	@Override
	public <V1, V2, V3, V4> Promise<MultipleResults4<V1, V2, V3, V4>, OneReject<Throwable>, MasterProgress> when(Future<V1> futureV1, Future<V2> futureV2, Future<V3> futureV3, Future<V4> futureV4) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);
		assertNotNull(futureV3, FUTURE_V3);
		assertNotNull(futureV4, FUTURE_V4);
		return new MasterDeferredObject4(when(futureV1), when(futureV2), when(futureV3), when(futureV4));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResults5<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(Future<V1> futureV1, Future<V2> futureV2, Future<V3> futureV3, Future<V4> futureV4, Future<V5> futureV5) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);
		assertNotNull(futureV3, FUTURE_V3);
		assertNotNull(futureV4, FUTURE_V4);
		assertNotNull(futureV5, FUTURE_V5);
		return new MasterDeferredObject5(when(futureV1), when(futureV2), when(futureV3), when(futureV4), when(futureV5));
	}

	@Override
	public <V1, V2, V3, V4, V5> Promise<MultipleResultsN<V1, V2, V3, V4, V5>, OneReject<Throwable>, MasterProgress> when(Future<V1> futureV1, Future<V2> futureV2, Future<V3> futureV3, Future<V4> futureV4, Future<V5> futureV5, Future<?> future6, Future<?>... futures) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);
		assertNotNull(futureV3, FUTURE_V3);
		assertNotNull(futureV4, FUTURE_V4);
		assertNotNull(futureV5, FUTURE_V5);
		assertNotNull(future6, "future6");

		Promise<V1, Throwable, ?> promise1 = when(futureV1);
		Promise<V2, Throwable, ?> promise2 = when(futureV2);
		Promise<V3, Throwable, ?> promise3 = when(futureV3);
		Promise<V4, Throwable, ?> promise4 = when(futureV4);
		Promise<V5, Throwable, ?> promise5 = when(futureV5);
		Promise<?, Throwable, ?> promise6 = when(future6);

		Promise[] promiseN = new Promise[futures.length];
		for (int i = 0; i < futures.length; i++) {
			promiseN[i] = when(futures[i]);
		}
		return new MasterDeferredObjectN(promise1, promise2, promise3, promise4, promise5, promise6, promiseN);
	}

	@Override
	public <D, F, P> Promise<D, F, P> when(Promise<D, F, P> promise) {
		assertNotNull(promise, "promise");
		return promise;
	}

	@Override
	public <P> Promise<Void, Throwable, P> when(DeferredRunnable<P> runnable) {
		assertNotNull(runnable, "runnable");
		return when(new DeferredFutureTask<Void, P>(runnable));
	}

	@Override
	public <D, P> Promise<D, Throwable, P> when(DeferredCallable<D, P> callable) {
		assertNotNull(callable, "callable");
		return when(new DeferredFutureTask<D, P>(callable));
	}

	@Override
	public Promise<Void, Throwable, Void> when(Runnable runnable) {
		assertNotNull(runnable, "runnable");
		return when(new DeferredFutureTask<Void, Void>(runnable));
	}

	@Override
	public <D> Promise<D, Throwable, Void> when(Callable<D> callable) {
		assertNotNull(callable, "callable");
		return when(new DeferredFutureTask<D, Void>(callable));
	}

	@Override
	public <D, P> Promise<D, Throwable, P> when(DeferredFutureTask<D, P> task) {
		assertNotNull(task, "task");
		if (task.getStartPolicy() == StartPolicy.AUTO
			|| (task.getStartPolicy() == StartPolicy.DEFAULT && isAutoSubmit())) {
			submit(task);
		}

		return task.promise();
	}

	@Override
	public <D> Promise<D, Throwable, Void> when(Future<D> future) {
		// make sure the task is automatically started
		return when(deferredCallableFor(future));
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(Runnable runnableV1, Runnable runnableV2, Runnable... runnables) {
		assertNotNull(runnableV1, RUNNABLE_V1);
		assertNotNull(runnableV2, RUNNABLE_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (runnables != null ? runnables.length : 0)];
		allTasks[0] = new DeferredFutureTask<Void, Void>(runnableV1);
		allTasks[1] = new DeferredFutureTask<Void, Void>(runnableV2);
		if (runnables != null) {
			for (int i = 0; i < runnables.length; i++) {
				allTasks[2 + i] = new DeferredFutureTask<Void, Void>(runnables[i]);
			}
		}

		return submitForSingle(allTasks);
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(Callable<?> callableV1, Callable<?> callableV2, Callable<?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (callables != null ? callables.length : 0)];
		allTasks[0] = new DeferredFutureTask<Object, Void>((Callable<Object>) callableV1);
		allTasks[1] = new DeferredFutureTask<Object, Void>((Callable<Object>) callableV2);
		if (callables != null) {
			for (int i = 0; i < callables.length; i++) {
				allTasks[2 + i] = new DeferredFutureTask<Object, Void>((Callable<Object>) callables[i]);
			}
		}

		return submitForSingle(allTasks);
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(DeferredRunnable<?> runnableV1, DeferredRunnable<?> runnableV2, DeferredRunnable<?>... runnables) {
		assertNotNull(runnableV1, RUNNABLE_V1);
		assertNotNull(runnableV2, RUNNABLE_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (runnables != null ? runnables.length : 0)];
		allTasks[0] = new DeferredFutureTask<Void, Void>(runnableV1);
		allTasks[1] = new DeferredFutureTask<Void, Void>(runnableV2);
		if (runnables != null) {
			for (int i = 0; i < runnables.length; i++) {
				allTasks[2 + i] = new DeferredFutureTask<Void, Void>(runnables[i]);
			}
		}

		return submitForSingle(allTasks);
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(DeferredCallable<?, ?> callableV1, DeferredCallable<?, ?> callableV2, DeferredCallable<?, ?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (callables != null ? callables.length : 0)];
		allTasks[0] = new DeferredFutureTask<Object, Object>((DeferredCallable<Object, Object>) callableV1);
		allTasks[1] = new DeferredFutureTask<Object, Object>((DeferredCallable<Object, Object>) callableV2);
		if (callables != null) {
			for (int i = 0; i < callables.length; i++) {
				allTasks[2 + i] = new DeferredFutureTask<Object, Object>((DeferredCallable<Object, Object>) callables[i]);
			}
		}

		return submitForSingle(allTasks);
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(Future<?> futureV1, Future<?> futureV2, Future<?>... futures) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (futures != null ? futures.length : 0)];
		allTasks[0] = new DeferredFutureTask<Object, Void>((DeferredCallable<Object, Void>) deferredCallableFor(futureV1));
		allTasks[1] = new DeferredFutureTask<Object, Void>((DeferredCallable<Object, Void>) deferredCallableFor(futureV2));
		if (futures != null) {
			for (int i = 0; i < futures.length; i++) {
				allTasks[2 + i] = new DeferredFutureTask<Object, Void>((DeferredCallable<Object, Void>) deferredCallableFor(futures[i]));
			}
		}

		return submitForSingle(allTasks);
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(DeferredFutureTask<?, ?> taskV1, DeferredFutureTask<?, ?> taskV2, DeferredFutureTask<?, ?>... tasks) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);

		DeferredFutureTask<?, ?>[] allTasks = new DeferredFutureTask[2 + (tasks != null ? tasks.length : 0)];
		allTasks[0] = taskV1;
		allTasks[1] = taskV2;
		if (tasks != null) {
			for (int i = 0; i < tasks.length; i++) {
				allTasks[2 + i] = tasks[i];
			}
		}

		return submitForSingle(allTasks);
	}

	protected Promise<OneResult<?>, OneReject<Throwable>, Void> submitForSingle(DeferredFutureTask<?, ?>[] tasks) {
		for (DeferredFutureTask<?, ?> task : tasks) {
			submit(task);
		}
		return new SingleDeferredObject(tasks);
	}

	protected <D> DeferredCallable<D, Void> deferredCallableFor(final Future<D> future) {
		assertNotNull(future, "future");

		return new DeferredCallable<D, Void>(StartPolicy.AUTO) {
			@Override
			public D call() throws Exception {
				try {
					return future.get();
				} catch (InterruptedException e) {
					throw e;
				} catch (ExecutionException e) {
					if (e.getCause() instanceof Exception) {
						throw (Exception) e.getCause();
					} else {
						throw e;
					}
				}
			}
		};
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(Runnable runnableV1, Runnable runnableV2, Runnable... runnables) {
		assertNotNull(runnableV1, RUNNABLE_V1);
		assertNotNull(runnableV2, RUNNABLE_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (runnables != null ? runnables.length : 0)];
		promises[0] = when(runnableV1);
		promises[1] = when(runnableV2);
		if (runnables != null) {
			for (int i = 0; i < runnables.length; i++) {
				promises[2 + i] = when(runnables[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(Callable<?> callableV1, Callable<?> callableV2, Callable<?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (callables != null ? callables.length : 0)];
		promises[0] = when(callableV1);
		promises[1] = when(callableV2);
		if (callables != null) {
			for (int i = 0; i < callables.length; i++) {
				promises[2 + i] = when(callables[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(DeferredRunnable<?> runnableV1, DeferredRunnable<?> runnableV2, DeferredRunnable<?>... runnables) {
		assertNotNull(runnableV1, RUNNABLE_V1);
		assertNotNull(runnableV2, RUNNABLE_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (runnables != null ? runnables.length : 0)];
		promises[0] = when(runnableV1);
		promises[1] = when(runnableV2);
		if (runnables != null) {
			for (int i = 0; i < runnables.length; i++) {
				promises[2 + i] = when(runnables[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(DeferredCallable<?, ?> callableV1, DeferredCallable<?, ?> callableV2, DeferredCallable<?, ?>... callables) {
		assertNotNull(callableV1, CALLABLE_V1);
		assertNotNull(callableV2, CALLABLE_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (callables != null ? callables.length : 0)];
		promises[0] = when(callableV1);
		promises[1] = when(callableV2);
		if (callables != null) {
			for (int i = 0; i < callables.length; i++) {
				promises[2 + i] = when(callables[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(Future<?> futureV1, Future<?> futureV2, Future<?>... futures) {
		assertNotNull(futureV1, FUTURE_V1);
		assertNotNull(futureV2, FUTURE_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (futures != null ? futures.length : 0)];
		promises[0] = when(futureV1);
		promises[1] = when(futureV2);
		if (futures != null) {
			for (int i = 0; i < futures.length; i++) {
				promises[2 + i] = when(futures[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(DeferredFutureTask<?, ?> taskV1, DeferredFutureTask<?, ?> taskV2, DeferredFutureTask<?, ?>... tasks) {
		assertNotNull(taskV1, TASK_V1);
		assertNotNull(taskV2, TASK_V2);

		Promise<?, ?, ?>[] promises = new Promise[2 + (tasks != null ? tasks.length : 0)];
		promises[0] = when(taskV1);
		promises[1] = when(taskV2);
		if (tasks != null) {
			for (int i = 0; i < tasks.length; i++) {
				promises[2 + i] = when(tasks[i]);
			}
		}

		return new AllValuesDeferredObject(promises);
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(Promise<?, ?, ?> promiseV1, Promise<?, ?, ?> promiseV2, Promise<?, ?, ?>... promises) {
		assertNotNull(promiseV1, "promiseV1");
		assertNotNull(promiseV2, "promiseV2");

		Promise<?, ?, ?>[] allPromises = new Promise[2 + (promises != null ? promises.length : 0)];
		allPromises[0] = promiseV1;
		allPromises[1] = promiseV2;
		if (promises != null) {
			System.arraycopy(promises, 0, allPromises, 2, promises.length);
		}

		return new AllValuesDeferredObject(allPromises);
	}

	@Deprecated
	protected void assertNotEmpty(Object[] objects) {
		if (objects == null || objects.length == 0) {
			throw new IllegalArgumentException(
				"Arguments is null or its length is empty");
		}
	}

	protected void assertNotNull(Object object, String name) {
		if (object == null) {
			throw new IllegalArgumentException("Argument '" + name + "' must not be null");
		}
	}

	@Override
	public Promise<MultipleResults, OneReject<?>, MasterProgress> when(Iterable<?> iterable) {
		if (iterable == null) {
			throw new IllegalArgumentException("Iterable is null");
		}

		Iterator<?> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Iterable is empty");
		}

		List<Object> items = new LinkedList<Object>();
		List<Promise<?, ?, ?>> promises = new ArrayList<Promise<?, ?, ?>>();

		// First pass, check each element to make sure it can be converted to a promise
		// This is done in 2 passes because we don't want to submit tasks but also throw an Exception because some
		// object was not able to convert. The method should succeed all or nothing.
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (!canPromise(object)) {
				throw new IllegalArgumentException("An item of type " + object.getClass().getName() + " cannot be converted to a Promise");
			}
			items.add(object);
		}

		// Second pass, now we know every object can be converted to a Promise, convert them
		for (Object item : items) {
			promises.add(toPromise(item));
		}

		return new MasterDeferredObjectUntypedN(promises.toArray(new Promise[promises.size()])).promise();
	}

	@Override
	public Promise<OneResult<?>, OneReject<Throwable>, Void> race(Iterable<?> iterable) {
		if (iterable == null) {
			throw new IllegalArgumentException("Iterable is null");
		}

		Iterator<?> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Iterable is empty");
		}

		List<Object> items = new LinkedList<Object>();
		List<DeferredFutureTask<?, ?>> allTasks = new ArrayList<DeferredFutureTask<?, ?>>();
		// First pass, check each element to make sure it can be converted to a promise
		// This is done in 2 passes because we don't want to submit tasks but also throw an Exception because some
		// object was not able to convert. The method should succeed all or nothing.
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (!canPromise(object)) {
				throw new IllegalArgumentException("An item of type " + object.getClass().getName() + " cannot be converted to a DeferredFutureTask");
			}
			// additional check: we can't use Promise to create a DeferredFutureTask
			if (object instanceof Promise) {
				throw new IllegalArgumentException("An item of type " + object.getClass().getName() + " cannot be converted to a DeferredFutureTask");
			}
			items.add(object);
		}

		// Second pass, now we know every object can be converted to a DeferredFutureTask, convert them
		for (Object item : items) {
			allTasks.add(toDeferredFutureTask(item));
		}

		return submitForSingle(allTasks.toArray(new DeferredFutureTask[allTasks.size()]));
	}

	@Override
	public Promise<AllValues, Throwable, MasterProgress> settle(Iterable<?> iterable) {
		if (iterable == null) {
			throw new IllegalArgumentException("Iterable is null");
		}

		Iterator<?> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			throw new IllegalArgumentException("Iterable is empty");
		}

		List<Object> items = new LinkedList<Object>();
		List<Promise<?, ?, ?>> promises = new ArrayList<Promise<?, ?, ?>>();

		// First pass, check each element to make sure it can be converted to a promise
		// This is done in 2 passes because we don't want to submit tasks but also throw an Exception because some
		// object was not able to convert. The method should succeed all or nothing.
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (!canPromise(object)) {
				throw new IllegalArgumentException("An item of type " + object.getClass().getName() + " cannot be converted to a Promise");
			}
			items.add(object);
		}

		// Second pass, now we know every object can be converted to a Promise, convert them
		for (Object item : items) {
			promises.add(toPromise(item));
		}

		return new AllValuesDeferredObject(promises.toArray(new Promise[promises.size()]));
	}

	@Override
	public <D, F, P> Promise<D, F, P> resolve(D resolve) {
		return new DeferredObject<D, F, P>().resolve(resolve).promise();
	}

	@Override
	public <D, F, P> Promise<D, F, P> reject(F reject) {
		return new DeferredObject<D, F, P>().reject(reject).promise();
	}

	protected boolean canPromise(Object o) {
		if (o instanceof DeferredFutureTask) {
			return true;
		} else if (o instanceof DeferredRunnable) {
			return true;
		} else if (o instanceof DeferredCallable) {
			return true;
		} else if (o instanceof Runnable) {
			return true;
		} else if (o instanceof Callable) {
			return true;
		} else if (o instanceof Future) {
			return true;
		} else if (o instanceof Promise) {
			return true;
		} else {
			return false;
		}
	}

	protected Promise<?, ?, ?> toPromise(Object o) {
		if (o instanceof DeferredFutureTask) {
			return when((DeferredFutureTask) o);
		} else if (o instanceof DeferredRunnable) {
			return when((DeferredRunnable) o);
		} else if (o instanceof DeferredCallable) {
			return when((DeferredCallable) o);
		} else if (o instanceof Runnable) {
			return when((Runnable) o);
		} else if (o instanceof Callable) {
			return when((Callable) o);
		} else if (o instanceof Future) {
			return when((Future) o);
		} else if (o instanceof Promise) {
			return (Promise) o;
		} else {
			throw new IllegalStateException("Unable to convert object to Promise. Should be guarded by canPromise()");
		}
	}

	protected DeferredFutureTask<?, ?> toDeferredFutureTask(Object o) {
		if (o instanceof DeferredFutureTask) {
			return (DeferredFutureTask) o;
		} else if (o instanceof DeferredRunnable) {
			return new DeferredFutureTask((DeferredRunnable) o);
		} else if (o instanceof DeferredCallable) {
			return new DeferredFutureTask((DeferredCallable) o);
		} else if (o instanceof Runnable) {
			return new DeferredFutureTask((Runnable) o);
		} else if (o instanceof Callable) {
			return new DeferredFutureTask((Callable) o);
		} else if (o instanceof Future) {
			return new DeferredFutureTask(deferredCallableFor((Future) o));
		} else {
			throw new IllegalStateException("Unable to convert object to DeferredFutureTask. Should be guarded by canPromise()");
		}
	}
}
