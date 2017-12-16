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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.CancellationHandler;
import org.jdeferred2.DeferredCallable;
import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DeferredRunnable;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.Promise.State;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnitParamsRunner.class)
public class CancelTaskTest extends AbstractDeferredTest {
	@Test
	@Parameters(method = "basicTasks")
	public void cancelTask(DeferredFutureTask<String, Void> deferredFutureTask) {
		final AtomicBoolean failWitness = new AtomicBoolean(false);

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask)
			.then(new DoneCallback<String>() {
				@Override
				public void onDone(String result) {
					fail("Shouldn't be called, because task was cancelled");
				}
			}).always(new AlwaysCallback<String, Throwable>() {
				@Override
				public void onAlways(State state, String resolved, Throwable rejected) {
					assertEquals(State.REJECTED, state);
					assertNull(resolved);
					assertTrue(rejected instanceof CancellationException);
				}
			}).fail(new FailCallback<Throwable>() {
				@Override
				public void onFail(Throwable result) {
					failWitness.set(true);
				}
			});

		deferredFutureTask.cancel(true);

		assertTrue(promise.isRejected());
		assertTrue(failWitness.get());
	}

	@Test
	public void explicitRejectionWithCancellationException() {
		final AtomicBoolean failWitness = new AtomicBoolean(false);

		DeferredObject<String, Throwable, Void> deferredObject = new DeferredObject<String, Throwable, Void>();

		Promise<String, Throwable, Void> promise = deferredObject.promise()
			.then(new DoneCallback<String>() {
				@Override
				public void onDone(String result) {
					fail("Shouldn't be called, because task was cancelled");
				}
			}).always(new AlwaysCallback<String, Throwable>() {
				@Override
				public void onAlways(State state, String resolved, Throwable rejected) {
					assertEquals(State.REJECTED, state);
					assertNull(resolved);
					assertTrue(rejected instanceof CancellationException);
				}
			}).fail(new FailCallback<Throwable>() {
				@Override
				public void onFail(Throwable result) {
					failWitness.set(true);
				}
			});
		deferredObject.reject(new CancellationException());

		assertTrue(promise.isRejected());
		assertTrue(failWitness.get());
	}

	@Test
	@Parameters(method = "tasksAsCancellationHandler")
	public void tasksImplementCancellationHandler(CancellationWitness cancellationWitness) throws Exception {
		DeferredFutureTask<String, Void> deferredFutureTask = createDeferredFutureTaskFromWitness(cancellationWitness);

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask);
		deferredFutureTask.cancel(true);

		assertTrue(promise.isRejected());
		assertTrue(cancellationWitness.invoked());
	}

	@Test
	@Parameters(method = "tasksWithExplicitCancellationHandler")
	public void taskswithExplicitCancellationHandler(CancellationWitness taskCancellationWitness, CancellationWitness cancellationWitness) throws Exception {
		DeferredFutureTask<String, Void> deferredFutureTask = createDeferredFutureTaskFromWitness(taskCancellationWitness, cancellationWitness);

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask);
		deferredFutureTask.cancel(true);

		assertTrue(promise.isRejected());
		assertFalse(taskCancellationWitness.invoked());
		assertTrue(cancellationWitness.invoked());
	}

	@SuppressWarnings("unchecked")
	private DeferredFutureTask<String, Void> createDeferredFutureTaskFromWitness(CancellationWitness cancellationWitness) {
		return createDeferredFutureTaskFromWitness(cancellationWitness, null);
	}

	@SuppressWarnings("unchecked")
	private DeferredFutureTask<String, Void> createDeferredFutureTaskFromWitness(CancellationWitness cancellationWitness, CancellationHandler cancellationHandler) {
		if (cancellationWitness instanceof DeferredRunnable) {
			return new DeferredFutureTask<String, Void>((DeferredRunnable<Void>) cancellationWitness, cancellationHandler);
		} else if (cancellationWitness instanceof DeferredCallable) {
			return new DeferredFutureTask<String, Void>((DeferredCallable<String, Void>) cancellationWitness, cancellationHandler);
		} else if (cancellationWitness instanceof Runnable) {
			return new DeferredFutureTask<String, Void>((Runnable) cancellationWitness, cancellationHandler);
		} else if (cancellationWitness instanceof Callable) {
			return new DeferredFutureTask<String, Void>((Callable<String>) cancellationWitness, cancellationHandler);
		}
		fail("invalid witness argument " + cancellationWitness.getClass());
		return null; // shakes fist at JUnit API because fail() neither declares a throws clause nor returns a Throwable
	}

	private DeferredFutureTask[] basicTasks() {
		return new DeferredFutureTask[]{
			new DeferredFutureTask<String, Void>(new Callable<String>() {
				@Override
				public String call() throws Exception {
					simulateWork();
					return "Hello";
				}
			}),
			new DeferredFutureTask<String, Void>(new Runnable() {
				@Override
				public void run() {
					simulateWork();
				}
			}),
			new DeferredFutureTask<String, Void>(new DeferredCallable<String, Void>() {
				@Override
				public String call() throws Exception {
					simulateWork();
					return "Hello";
				}
			}),
			new DeferredFutureTask<String, Void>(new DeferredRunnable<Void>() {
				@Override
				public void run() {
					simulateWork();
				}
			})
		};
	}

	private CancellationWitness[] tasksAsCancellationHandler() {
		return new CancellationWitness[]{
			new CancellationHandlerCallable(),
			new CancellationHandlerRunnable(),
			new CancellationHandlerDeferredCallable(),
			new CancellationHandlerDeferredRunnable()
		};
	}

	private CancellationWitness[][] tasksWithExplicitCancellationHandler() {
		return new CancellationWitness[][]{
			new CancellationWitness[]{new CancellationHandlerCallable(), new DefaultCancellationHandler()},
			new CancellationWitness[]{new CancellationHandlerRunnable(), new DefaultCancellationHandler()},
			new CancellationWitness[]{new CancellationHandlerDeferredCallable(), new DefaultCancellationHandler()},
			new CancellationWitness[]{new CancellationHandlerDeferredRunnable(), new DefaultCancellationHandler()}
		};
	}

	private static void simulateWork() {
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}

	public interface CancellationWitness extends CancellationHandler {
		boolean invoked();
	}

	public static class DefaultCancellationHandler implements CancellationWitness {
		private volatile boolean invoked;

		@Override
		public void onCancel() {
			invoked = true;
		}

		@Override
		public boolean invoked() {
			return invoked;
		}
	}

	public static class CancellationHandlerCallable extends DefaultCancellationHandler implements Callable<String> {
		@Override
		public String call() throws Exception {
			simulateWork();
			return "Hello";
		}
	}

	public static class CancellationHandlerRunnable extends DefaultCancellationHandler implements Runnable {
		@Override
		public void run() {
			simulateWork();
		}
	}

	public static class CancellationHandlerDeferredCallable extends DeferredCallable<String, Void> implements CancellationWitness {
		private volatile boolean invoked;

		@Override
		public String call() throws Exception {
			simulateWork();
			return "Hello";
		}

		@Override
		public void onCancel() {
			invoked = true;
		}

		@Override
		public boolean invoked() {
			return invoked;
		}
	}

	public static class CancellationHandlerDeferredRunnable extends DeferredRunnable<Void> implements CancellationWitness {
		private volatile boolean invoked;

		@Override
		public void run() {
			simulateWork();
		}

		@Override
		public void onCancel() {
			invoked = true;
		}

		@Override
		public boolean invoked() {
			return invoked;
		}
	}
}
