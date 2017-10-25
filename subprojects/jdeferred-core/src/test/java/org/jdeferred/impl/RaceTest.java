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

import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RaceTest extends AbstractDeferredTest {
	@Test
	public void raceDeferredFutureTaskAndResolve() {
		DeferredFutureTask<Integer, Void> task1 = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(0));
		DeferredFutureTask<Integer, Void> task2 = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(1));
		DeferredFutureTask<Integer, Void>[] tasks = new DeferredFutureTask[3];
		for (int i = 0; i < 3; i++) {
			tasks[i] = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(2 + i));
		}

		Promise<Integer, Throwable, Void>[] promises = new Promise[2 + tasks.length];
		promises[0] = task1.promise();
		promises[1] = task2.promise();
		for (int i = 0; i < 3; i++) {
			promises[2 + i] = tasks[i].promise();
		}

		final AtomicInteger index = new AtomicInteger(0);

		deferredManager.race(task1, task2, tasks)
			.done(new DoneCallback<OneResult<?>>() {
				public void onDone(OneResult<?> result) {
					index.set(result.getIndex());
				}
			}).fail(new FailCallback<OneReject<Throwable>>() {
			@Override
			public void onFail(OneReject<Throwable> result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		// promise at $index == RESOLVED
		// all other promises should be REJECTED
		for (int i = 0; i < promises.length; i++) {
			Promise<Integer, Throwable, Void> promise = promises[i];
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should be resolved", promise.isResolved());
			} else {
				assertTrue("Promise at index " + index.get() + " should be rejected", promise.isRejected());
			}
		}
	}

	@Test
	public void raceDeferredFutureTaskAndReject() {
		DeferredFutureTask<Integer, Void> task1 = new DeferredFutureTask<Integer, Void>(new RejectingCallable(0));
		DeferredFutureTask<Integer, Void> task2 = new DeferredFutureTask<Integer, Void>(new RejectingCallable(1));
		DeferredFutureTask<Integer, Void>[] tasks = new DeferredFutureTask[3];
		for (int i = 0; i < 3; i++) {
			tasks[i] = new DeferredFutureTask<Integer, Void>(new RejectingCallable(2 + i));
		}

		Promise<Integer, Throwable, Void>[] promises = new Promise[2 + tasks.length];
		promises[0] = task1.promise();
		promises[1] = task2.promise();
		for (int i = 0; i < 3; i++) {
			promises[2 + i] = tasks[i].promise();
		}

		final AtomicInteger index = new AtomicInteger(0);
		final IndexedRuntimeException[] exception = new IndexedRuntimeException[1];

		deferredManager.race(task1, task2, tasks)
			.done(new DoneCallback<OneResult<?>>() {
				public void onDone(OneResult<?> result) {
					fail("Shouldn't be here");
				}
			}).fail(new FailCallback<OneReject<Throwable>>() {
			@Override
			public void onFail(OneReject<Throwable> result) {
				index.set(result.getIndex());
				exception[0] = (IndexedRuntimeException) result.getReject();
			}
		});

		waitForCompletion();

		// promise at $index == REJECTED & rejectedValue should have $index
		// all other promises should be REJECTED
		for (int i = 0; i < promises.length; i++) {
			Promise<Integer, Throwable, Void> promise = promises[i];
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should be rejected with " + index, promise.isRejected());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should be rejected", promise.isRejected());
			}
		}
	}

	private static class ResolvingCallable implements Callable<Integer> {
		protected static final SecureRandom RANDOM = new SecureRandom();
		protected final int index;

		public ResolvingCallable(int index) {
			this.index = index;
		}

		@Override
		public Integer call() throws Exception {
			Thread.sleep(500);
			Thread.sleep(RANDOM.nextInt(100));
			return index;
		}
	}

	private static class RejectingCallable extends ResolvingCallable {
		public RejectingCallable(int index) {
			super(index);
		}

		@Override
		public Integer call() throws Exception {
			Thread.sleep(500);
			Thread.sleep(RANDOM.nextInt(100));
			throw new IndexedRuntimeException(index);
		}
	}

	private static class IndexedRuntimeException extends RuntimeException {
		private final int index;

		private IndexedRuntimeException(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}
	}
}
