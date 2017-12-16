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

import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.multiple.OneReject;
import org.jdeferred2.multiple.OneResult;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings({"unchecked", "rawtypes"})
public class RaceTest extends AbstractDeferredTest {
	@Test
	public void raceRunnableAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		ResolvingRunnable task1 = new ResolvingRunnable(0, 200, invocationWitness[0]);
		ResolvingRunnable task2 = new ResolvingRunnable(1, 200, invocationWitness[1]);
		ResolvingRunnable[] tasks = new ResolvingRunnable[3];
		tasks[0] = new ResolvingRunnable(2, 100, invocationWitness[2]);
		tasks[1] = new ResolvingRunnable(3, 200, invocationWitness[3]);
		tasks[2] = new ResolvingRunnable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceRunnableAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		RejectingRunnable task1 = new RejectingRunnable(0, 200, invocationWitness[0]);
		RejectingRunnable task2 = new RejectingRunnable(1, 200, invocationWitness[1]);
		RejectingRunnable[] tasks = new RejectingRunnable[3];
		tasks[0] = new RejectingRunnable(2, 100, invocationWitness[2]);
		tasks[1] = new RejectingRunnable(3, 200, invocationWitness[3]);
		tasks[2] = new RejectingRunnable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceCallableAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		ResolvingCallable task1 = new ResolvingCallable(0, 200, invocationWitness[0]);
		ResolvingCallable task2 = new ResolvingCallable(1, 200, invocationWitness[1]);
		ResolvingCallable[] tasks = new ResolvingCallable[3];
		tasks[0] = new ResolvingCallable(2, 100, invocationWitness[2]);
		tasks[1] = new ResolvingCallable(3, 200, invocationWitness[3]);
		tasks[2] = new ResolvingCallable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceCallableAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		RejectingCallable task1 = new RejectingCallable(0, 200, invocationWitness[0]);
		RejectingCallable task2 = new RejectingCallable(1, 200, invocationWitness[1]);
		RejectingCallable[] tasks = new RejectingCallable[3];
		tasks[0] = new RejectingCallable(2, 100, invocationWitness[2]);
		tasks[1] = new RejectingCallable(3, 200, invocationWitness[3]);
		tasks[2] = new RejectingCallable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceDeferredRunnableAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		ResolvingDeferredRunnable task1 = new ResolvingDeferredRunnable(0, 200, invocationWitness[0]);
		ResolvingDeferredRunnable task2 = new ResolvingDeferredRunnable(1, 200, invocationWitness[1]);
		ResolvingDeferredRunnable[] tasks = new ResolvingDeferredRunnable[3];
		tasks[0] = new ResolvingDeferredRunnable(2, 100, invocationWitness[2]);
		tasks[1] = new ResolvingDeferredRunnable(3, 200, invocationWitness[3]);
		tasks[2] = new ResolvingDeferredRunnable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceDeferredRunnableAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		RejectingDeferredRunnable task1 = new RejectingDeferredRunnable(0, 200, invocationWitness[0]);
		RejectingDeferredRunnable task2 = new RejectingDeferredRunnable(1, 200, invocationWitness[1]);
		RejectingDeferredRunnable[] tasks = new RejectingDeferredRunnable[3];
		tasks[0] = new RejectingDeferredRunnable(2, 100, invocationWitness[2]);
		tasks[1] = new RejectingDeferredRunnable(3, 200, invocationWitness[3]);
		tasks[2] = new RejectingDeferredRunnable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceDeferredCallableAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		ResolvingDeferredCallable task1 = new ResolvingDeferredCallable(0, 200, invocationWitness[0]);
		ResolvingDeferredCallable task2 = new ResolvingDeferredCallable(1, 200, invocationWitness[1]);
		ResolvingDeferredCallable[] tasks = new ResolvingDeferredCallable[3];
		tasks[0] = new ResolvingDeferredCallable(2, 100, invocationWitness[2]);
		tasks[1] = new ResolvingDeferredCallable(3, 200, invocationWitness[3]);
		tasks[2] = new ResolvingDeferredCallable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceDeferredCallableAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		RejectingDeferredCallable task1 = new RejectingDeferredCallable(0, 200, invocationWitness[0]);
		RejectingDeferredCallable task2 = new RejectingDeferredCallable(1, 200, invocationWitness[1]);
		RejectingDeferredCallable[] tasks = new RejectingDeferredCallable[3];
		tasks[0] = new RejectingDeferredCallable(2, 100, invocationWitness[2]);
		tasks[1] = new RejectingDeferredCallable(3, 200, invocationWitness[3]);
		tasks[2] = new RejectingDeferredCallable(4, 200, invocationWitness[4]);

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Ignore("Waits indefinitely")
	@Test
	public void raceFutureAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		Future<Integer> task1 = new FutureTask<Integer>(new ResolvingCallable(0, 200, invocationWitness[0]));
		Future<Integer> task2 = new FutureTask<Integer>(new ResolvingCallable(1, 200, invocationWitness[1]));
		Future<Integer>[] tasks = new Future[3];
		tasks[0] = new FutureTask<Integer>(new ResolvingCallable(2, 100, invocationWitness[2]));
		tasks[1] = new FutureTask<Integer>(new ResolvingCallable(3, 200, invocationWitness[3]));
		tasks[2] = new FutureTask<Integer>(new ResolvingCallable(4, 200, invocationWitness[4]));

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Ignore("Waits indefinitely")
	@Test
	public void raceFutureAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[5];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		Future<Integer> task1 = new FutureTask<Integer>(new RejectingCallable(0, 200, invocationWitness[0]));
		Future<Integer> task2 = new FutureTask<Integer>(new RejectingCallable(1, 200, invocationWitness[1]));
		Future<Integer>[] tasks = new Future[3];
		tasks[0] = new FutureTask<Integer>(new RejectingCallable(2, 100, invocationWitness[2]));
		tasks[1] = new FutureTask<Integer>(new RejectingCallable(3, 200, invocationWitness[3]));
		tasks[2] = new FutureTask<Integer>(new RejectingCallable(4, 200, invocationWitness[4]));

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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < tasks.length; i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

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

	@Test
	public void raceIterableAndResolve() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[10];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		int j = 0;
		List<Object> iterable = new ArrayList<Object>();
		iterable.add(new ResolvingRunnable(j++, 200, invocationWitness[0]));
		iterable.add(new ResolvingRunnable(j++, 200, invocationWitness[1]));
		iterable.add(new ResolvingCallable(j++, 100, invocationWitness[2]));
		iterable.add(new ResolvingCallable(j++, 200, invocationWitness[3]));
		iterable.add(new ResolvingDeferredRunnable(j++, 200, invocationWitness[4]));
		iterable.add(new ResolvingDeferredRunnable(j++, 200, invocationWitness[5]));
		iterable.add(new ResolvingDeferredCallable(j++, 200, invocationWitness[6]));
		iterable.add(new ResolvingDeferredCallable(j++, 200, invocationWitness[7]));
		iterable.add(new DeferredFutureTask<Integer, Void>(new ResolvingCallable(j++, 200, invocationWitness[8])));
		iterable.add(new DeferredFutureTask<Integer, Void>(new ResolvingCallable(j++, 200, invocationWitness[9])));

		final AtomicInteger index = new AtomicInteger(0);

		deferredManager.race(iterable)
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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < iterable.size(); i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}

	@Test
	public void raceIterableAndReject() {
		AtomicBoolean[] invocationWitness = new AtomicBoolean[10];
		Arrays.fill(invocationWitness, new AtomicBoolean());

		int j = 0;
		List<Object> iterable = new ArrayList<Object>();
		iterable.add(new RejectingRunnable(j++, 200, invocationWitness[0]));
		iterable.add(new RejectingRunnable(j++, 200, invocationWitness[1]));
		iterable.add(new RejectingCallable(j++, 100, invocationWitness[2]));
		iterable.add(new RejectingCallable(j++, 200, invocationWitness[3]));
		iterable.add(new RejectingDeferredRunnable(j++, 200, invocationWitness[4]));
		iterable.add(new RejectingDeferredRunnable(j++, 200, invocationWitness[5]));
		iterable.add(new RejectingDeferredCallable(j++, 200, invocationWitness[6]));
		iterable.add(new RejectingDeferredCallable(j++, 200, invocationWitness[7]));
		iterable.add(new DeferredFutureTask<Integer, Void>(new RejectingCallable(j++, 200, invocationWitness[8])));
		iterable.add(new DeferredFutureTask<Integer, Void>(new RejectingCallable(j++, 200, invocationWitness[9])));

		final AtomicInteger index = new AtomicInteger(0);
		final IndexedRuntimeException[] exception = new IndexedRuntimeException[1];

		deferredManager.race(iterable)
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

		// promise at index == 2 => invoked
		// all other promises should not be invoked
		for (int i = 0; i < iterable.size(); i++) {
			if (i == index.get()) {
				assertTrue("Promise at index " + index.get() + " should have been invoked", invocationWitness[i].get());
				assertEquals(i, exception[0].getIndex());
			} else {
				assertTrue("Promise at index " + index.get() + " should have not been invoked", invocationWitness[i].get());
			}
		}
	}
}
