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
import org.jdeferred2.DeferredRunnable;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.multiple.AllValues;
import org.jdeferred2.multiple.OneReject;
import org.jdeferred2.multiple.OneResult;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SettleTest extends AbstractDeferredTest {
	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_Runnable() {
		Runnable task1 = new ResolvingRunnable(0);
		Runnable task2 = new RejectingRunnable(1);
		Runnable[] tasks = new Runnable[4];
		for (int i = 0; i < 4; i++) {
			tasks[i] = i % 2 == 0 ? new ResolvingRunnable(2 + i) : new RejectingRunnable(2 + i);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		AllValues allValues = values[0];
		// promise at $index % 2 == 0 => RESOLVED
		// promise at $index % 2 == 1 => REJECTED
		for (int i = 0; i < allValues.size(); i++) {
			if (i % 2 == 0) {
				assertTrue("OneValue at index " + i + " should be of type OneResult", allValues.get(i) instanceof OneResult);
				// Runnable does not resolve a value
				// assertEquals("Value at index " + i + " should be equal to " + i, i, allValues.get(i).getValue());
			} else {
				assertTrue("OneValue at index " + i + " should be of type OneReject", allValues.get(i) instanceof OneReject);
				assertTrue("Value at index " + i + " should be of type IndexedRuntimeException", allValues.get(i).getValue() instanceof IndexedRuntimeException);
			}
		}
	}

	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_Callable() {
		ResolvingCallable task1 = new ResolvingCallable(0);
		ResolvingCallable task2 = new RejectingCallable(1);
		ResolvingCallable[] tasks = new ResolvingCallable[4];
		for (int i = 0; i < 4; i++) {
			tasks[i] = i % 2 == 0 ? new ResolvingCallable(2 + i) : new RejectingCallable(2 + i);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		verifyAllValues(values[0]);
	}

	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_DeferredRunnable() {
		DeferredRunnable<Void> task1 = new ResolvingDeferredRunnable(0);
		DeferredRunnable<Void> task2 = new RejectingDeferredRunnable(1);
		DeferredRunnable<Void>[] tasks = new DeferredRunnable[4];
		for (int i = 0; i < 4; i++) {
			tasks[i] = i % 2 == 0 ? new ResolvingDeferredRunnable(2 + i) : new RejectingDeferredRunnable(2 + i);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		AllValues allValues = values[0];
		// promise at $index % 2 == 0 => RESOLVED
		// promise at $index % 2 == 1 => REJECTED
		for (int i = 0; i < allValues.size(); i++) {
			if (i % 2 == 0) {
				assertTrue("OneValue at index " + i + " should be of type OneResult", allValues.get(i) instanceof OneResult);
				// Runnable does not resolve a value
				// assertEquals("Value at index " + i + " should be equal to " + i, i, allValues.get(i).getValue());
			} else {
				assertTrue("OneValue at index " + i + " should be of type OneReject", allValues.get(i) instanceof OneReject);
				assertTrue("Value at index " + i + " should be of type IndexedRuntimeException", allValues.get(i).getValue() instanceof IndexedRuntimeException);
			}
		}
	}

	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_DeferredCallable() {
		DeferredCallable<Integer, Void> task1 = new ResolvingDeferredCallable(0);
		DeferredCallable<Integer, Void> task2 = new RejectingDeferredCallable(1);
		DeferredCallable<Integer, Void>[] tasks = new DeferredCallable[4];
		for (int i = 0; i < 4; i++) {
			tasks[i] = i % 2 == 0 ? new ResolvingDeferredCallable(2 + i) : new RejectingDeferredCallable(2 + i);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		verifyAllValues(values[0]);
	}

	@Ignore("Waits indefinitely")
	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_Future() {
		Future<Integer> task1 = new FutureTask<Integer>(new ResolvingCallable(0));
		Future<Integer> task2 = new FutureTask<Integer>(new RejectingCallable(1));
		Future<Integer>[] tasks = new Future[4];
		for (int i = 0; i < 4; i++) {
			tasks[i] = new FutureTask<Integer>(i % 2 == 0 ? new ResolvingCallable(2 + i) : new RejectingCallable(2 + i));
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		verifyAllValues(values[0]);
	}

	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_DeferredFutureTask() {
		DeferredFutureTask<Integer, Void> task1 = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(0));
		DeferredFutureTask<Integer, Void> task2 = new DeferredFutureTask<Integer, Void>(new RejectingCallable(1));
		DeferredFutureTask<Integer, Void>[] tasks = new DeferredFutureTask[4];
		for (int i = 0; i < 4; i++) {
			Callable<Integer> callable = i % 2 == 0 ? new ResolvingCallable(2 + i) : new RejectingCallable(2 + i);
			tasks[i] = new DeferredFutureTask<Integer, Void>(callable);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(task1, task2, tasks)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		verifyAllValues(values[0]);
	}

	@Test
	public void settleDeferredFutureTaskWith3ResolvesAnd3Rejects_Promise() {
		DeferredFutureTask<Integer, Void> task1 = new DeferredFutureTask<Integer, Void>(new ResolvingCallable(0));
		DeferredFutureTask<Integer, Void> task2 = new DeferredFutureTask<Integer, Void>(new RejectingCallable(1));
		DeferredFutureTask<Integer, Void>[] tasks = new DeferredFutureTask[4];
		for (int i = 0; i < 4; i++) {
			Callable<Integer> callable = i % 2 == 0 ? new ResolvingCallable(2 + i) : new RejectingCallable(2 + i);
			tasks[i] = new DeferredFutureTask<Integer, Void>(callable);
		}

		Promise<Integer, Throwable, Void> promise1 = deferredManager.when(task1);
		Promise<Integer, Throwable, Void> promise2 = deferredManager.when(task2);
		Promise<Integer, Throwable, Void>[] promises = new Promise[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			promises[i] = deferredManager.when(tasks[i]);
		}

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(promise1, promise2, promises)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		verifyAllValues(values[0]);
	}

	@Test
	public void settleIterableWith3ResolvesAnd3Rejects() {
		int index = 0;
		List<Object> iterable = new ArrayList<Object>();
		iterable.add(new ResolvingRunnable(index++));
		iterable.add(new RejectingRunnable(index++));
		iterable.add(new ResolvingDeferredRunnable(index++));
		iterable.add(new RejectingDeferredRunnable(index++));
		iterable.add(new ResolvingCallable(index++));
		iterable.add(new RejectingCallable(index++));
		iterable.add(new ResolvingDeferredCallable(index++));
		iterable.add(new RejectingDeferredCallable(index++));
		iterable.add(new DeferredFutureTask<Integer, Void>(new ResolvingCallable(index++)));
		iterable.add(new DeferredFutureTask<Integer, Void>(new RejectingCallable(index++)));
		// iterable.add(new FutureTask<Integer>(new ResolvingCallable(index++)));
		// iterable.add(new FutureTask<Integer>(new RejectingCallable(index++)));

		final AllValues[] values = new AllValues[1];
		deferredManager.settle(iterable)
			.done(new DoneCallback<AllValues>() {
				@Override
				public void onDone(AllValues result) {
					values[0] = result;
				}
			}).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				fail("Shouldn't be here");
			}
		});

		waitForCompletion();

		// skip verifying the first 4 values as they are produced by runnables
		verifyAllValues(values[0], 4);
	}

	private void verifyAllValues(AllValues allValues) {
		verifyAllValues(allValues, 0);
	}

	private void verifyAllValues(AllValues allValues, int offset) {
		// promise at $index % 2 == 0 => RESOLVED
		// promise at $index % 2 == 1 => REJECTED
		for (int i = offset; i < allValues.size(); i++) {
			if (i % 2 == 0) {
				assertTrue("OneValue at index " + i + " should be of type OneResult", allValues.get(i) instanceof OneResult);
				assertEquals("Value at index " + i + " should be equal to " + i, i, allValues.get(i).getValue());
			} else {
				assertTrue("OneValue at index " + i + " should be of type OneReject", allValues.get(i) instanceof OneReject);
				assertTrue("Value at index " + i + " should be of type IndexedRuntimeException", allValues.get(i).getValue() instanceof IndexedRuntimeException);
			}
		}
	}
}
