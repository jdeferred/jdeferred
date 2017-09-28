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
package org.jdeferred.impl;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.CancelCallback;
import org.jdeferred.DeferredCallable;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnitParamsRunner.class)
public class CancelTaskTest extends AbstractDeferredTest {
	@Test
	@Parameters(method = "basicTasks")
	public void testCancelTask(DeferredFutureTask<String, Void> deferredFutureTask) {
		final AtomicBoolean cancelWitness = new AtomicBoolean(false);

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask)
			.then(new DoneCallback<String>() {
				@Override
				public void onDone(String result) {
					fail("Shouldn't be called, because task was cancelled");
				}
			}).always(new AlwaysCallback<String, Throwable>() {
				@Override
				public void onAlways(State state, String resolved, Throwable rejected) {
					assertEquals(State.CANCELLED, state);
					assertNull(resolved);
					assertNull(rejected);
				}
			}).cancel(new CancelCallback() {
				@Override
				public void onCancel() {
					cancelWitness.set(true);
				}
			});

		deferredFutureTask.cancel(true);

		assertTrue(promise.isCancelled());
		assertTrue(cancelWitness.get());
	}

	@Test
	@Parameters(method = "basicTasks")
	public void cancelTaskWithNoRegisteredCancelCallback(DeferredFutureTask<String, Void> deferredFutureTask) {
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
					assertEquals(State.CANCELLED, state);
					assertNull(resolved);
					assertNull(rejected);
				}
			}).fail(new FailCallback<Throwable>() {
				@Override
				public void onFail(Throwable result) {
					failWitness.set(true);
				}
			});

		deferredFutureTask.cancel(true);

		assertTrue(promise.isCancelled());
		assertTrue(failWitness.get());
	}

	@Test
	public void explicitRejectionWithCancellationExceptionAndFailCallback() {
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
					assertEquals(State.CANCELLED, state);
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

		assertTrue(promise.isCancelled());
		assertTrue(failWitness.get());
	}


	@Test
	public void explicitRejectionWithCancellationExceptionAndCancelCallback() {
		final AtomicBoolean cancelWitness = new AtomicBoolean(false);

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
					assertEquals(State.CANCELLED, state);
					assertNull(resolved);
					assertTrue(rejected instanceof CancellationException);
				}
			}).cancel(new CancelCallback() {
				@Override
				public void onCancel() {
					cancelWitness.set(true);
				}
			});
		deferredObject.reject(new CancellationException());

		assertTrue(promise.isCancelled());
		assertTrue(cancelWitness.get());
	}

	public DeferredFutureTask[] basicTasks() {
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

	private static void simulateWork() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
