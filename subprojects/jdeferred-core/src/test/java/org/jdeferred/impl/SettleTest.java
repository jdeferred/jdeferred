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
import org.jdeferred.multiple.MultipleValues;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SettleTest extends AbstractDeferredTest {
	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects() {
		DeferredFutureTask<Integer, Void> task1 = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(0));
		DeferredFutureTask<Integer, Void> task2 = new DeferredFutureTask<Integer, Void>(new RejectingCallable(1));
		DeferredFutureTask<Integer, Void>[] tasks = new DeferredFutureTask[4];
		for (int i = 0; i < 4; i++) {
			Callable<Integer> callable = i % 2 == 0 ? new ResolvingCallable(2 + i) : new RejectingCallable(2 + i);
			tasks[i] = new DeferredFutureTask<Integer, Void>(callable);
		}

		Promise<Integer, Throwable, Void>[] promises = new Promise[2 + tasks.length];
		promises[0] = task1.promise();
		promises[1] = task2.promise();
		for (int i = 0; i < 4; i++) {
			promises[2 + i] = tasks[i].promise();
		}

		final MultipleValues[] values = new MultipleValues[1];

		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<MultipleValues>() {
				@Override
				public void onDone(MultipleValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		// promise at $index % 2 == 0 => RESOLVED
		// promise at $index % 2 == 1 => REJECTED
		for (int i = 0; i < promises.length; i++) {
			Promise<Integer, Throwable, Void> promise = promises[i];
			if (i % 2 == 0) {
				assertTrue("Promise at index " + i + " should be resolved", promise.isResolved());
				assertTrue("OneOf at index " + i + " should be of type OneResult", values[0].get(i) instanceof OneResult);
				assertEquals("Value at index " + i + " should be equal to " + i, i, values[0].get(i).getValue());
			} else {
				assertTrue("Promise at index " + i + " should be rejected", promise.isRejected());
				assertTrue("OneOf at index " + i + " should be of type OneReject", values[0].get(i) instanceof OneReject);
				assertTrue("Value at index " + i + " should be of type IndexedRuntimeException", values[0].get(i).getValue() instanceof IndexedRuntimeException);
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