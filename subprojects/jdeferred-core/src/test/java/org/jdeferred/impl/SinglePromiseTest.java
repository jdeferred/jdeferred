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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DeferredCallable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SinglePromiseTest extends AbstractDeferredTest {
	@Test
	public void testDoneWait() {
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();
		final AtomicInteger failCount = new AtomicInteger();
		deferredManager.when(successCallable(100, 1000))
				.done(new DoneCallback() {
					public void onDone(Object result) {
						Assert.assertEquals(result, 100);
						holder.set((Integer) result);
					}
				}).fail(new FailCallback() {
					public void onFail(Object result) {
						failCount.incrementAndGet();
					}
				});

		waitForCompletion();
		holder.assertEquals(100);
		Assert.assertEquals(0, failCount.get());
	}

	@Test
	public void testFailWait() {
		final AtomicInteger failCount = new AtomicInteger();
		final AtomicInteger doneCount = new AtomicInteger();
		deferredManager
				.when(failedCallable(new RuntimeException("oops"), 1000))
				.done(new DoneCallback() {
					public void onDone(Object result) {
						doneCount.incrementAndGet();
					}
				}).fail(new FailCallback() {
					public void onFail(Object result) {
						failCount.incrementAndGet();
					}
				});

		waitForCompletion();
		Assert.assertEquals(0, doneCount.get());
		Assert.assertEquals(1, failCount.get());
	}

	@Test
	public void testFailNoWait() {
		final AtomicInteger counter = new AtomicInteger();
		deferredManager.when(failedCallable(new RuntimeException("oops"), 0))
				.done(new DoneCallback() {
					public void onDone(Object result) {
						Assert.fail("Should not be here");
					}
				}).fail(new FailCallback<Throwable>() {
					public void onFail(Throwable result) {
						counter.incrementAndGet();
						Assert.assertEquals("oops", result.getMessage());
					}
				});

		waitForCompletion();
		Assert.assertEquals(1, counter.get());
	}

	@Test
	public void testDoneNoWait() {
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();
		final AtomicInteger failCount = new AtomicInteger();
		deferredManager.when(successCallable(100, 0)).done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(result, 100);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				failCount.incrementAndGet();
			}
		});

		waitForCompletion();
		holder.assertEquals(100);
		Assert.assertEquals(0, failCount.get());
	}

	@Test
	public void testAlwaysDone() {
		final AtomicInteger failCount = new AtomicInteger();
		final AtomicInteger alwaysCount = new AtomicInteger();
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();

		deferredManager.when(successCallable(100, 1000))
				.done(new DoneCallback() {
					public void onDone(Object result) {
						Assert.assertEquals(result, 100);
						holder.set((Integer) result);
					}
				}).fail(new FailCallback() {
					public void onFail(Object result) {
						failCount.incrementAndGet();
					}
				}).always(new AlwaysCallback<Integer, Throwable>() {
					@Override
					public void onAlways(State state, Integer resolved,
							Throwable rejected) {
						Assert.assertEquals(State.RESOLVED, state);
						Assert.assertEquals((Integer) 100, resolved);
						alwaysCount.incrementAndGet();
					}
				});

		waitForCompletion();
		holder.assertEquals(100);
		Assert.assertEquals(0, failCount.get());
		Assert.assertEquals(1, alwaysCount.get());
	}

	@Test
	public void testAlwaysFail() {
		final AtomicInteger doneCount = new AtomicInteger();
		final AtomicInteger failCount = new AtomicInteger();
		final AtomicInteger alwaysCount = new AtomicInteger();

		deferredManager
				.when(failedCallable(new RuntimeException("oops"), 1000))
				.done(new DoneCallback() {
					public void onDone(Object result) {
						doneCount.incrementAndGet();
					}
				}).fail(new FailCallback<Throwable>() {
					public void onFail(Throwable result) {
						Assert.assertEquals("oops", result.getMessage());
						failCount.incrementAndGet();
					}
				}).always(new AlwaysCallback<Void, Throwable>() {
					@Override
					public void onAlways(State state, Void resolved,
							Throwable rejected) {
						Assert.assertEquals(State.REJECTED, state);
						Assert.assertEquals("oops", rejected.getMessage());
						alwaysCount.incrementAndGet();
					}
				});

		waitForCompletion();
		Assert.assertEquals(0, doneCount.get());
		Assert.assertEquals(1, failCount.get());
		Assert.assertEquals(1, alwaysCount.get());
	}

	@Test
	public void testPorgressWait() {
		final AtomicInteger failCount = new AtomicInteger();
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();

		DeferredCallable<Integer, Integer> task = new DeferredCallable<Integer, Integer>() {
			public Integer call() {
				int sum = 0;
				for (int i = 1; i <= 10; i++) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					notify(i);
					sum += i;
				}

				return sum;
			}
		};

		// single threaded only
		final AtomicInteger count = new AtomicInteger(0);

		deferredManager.when(task).done(new DoneCallback() {
			public void onDone(Object result) {
				Assert.assertEquals(55, result);
				holder.set((Integer) result);
			}
		}).fail(new FailCallback() {
			public void onFail(Object result) {
				failCount.incrementAndGet();
			}
		}).progress(new ProgressCallback() {
			public void onProgress(Object progress) {
				count.incrementAndGet();
			}
		});

		waitForCompletion();
		holder.assertEquals(55);
		Assert.assertEquals(10, count.get());
		Assert.assertEquals(0, failCount.get());
	}

	@Test
	public void testFuture() {
		ExecutorService es = deferredManager.getExecutorService();
		Future<Integer> future = es.submit(successCallable(999, 100));
		final AtomicInteger doneCount = new AtomicInteger();
		deferredManager.when(future).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				Assert.assertEquals((Integer) 999, result);
				doneCount.incrementAndGet();
			}
		});

		Future<Void> failedFuture = es.submit(failedCallable(
				new RuntimeException("oops"), 300));
		final AtomicInteger failCount = new AtomicInteger();
		deferredManager.when(failedFuture).fail(new FailCallback<Throwable>() {
			@Override
			public void onFail(Throwable result) {
				Assert.assertEquals("oops", result.getCause());
				failCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
	}

	@Test
	public void testWait() {
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();
		final AtomicInteger failCount = new AtomicInteger();
		Promise<Integer, Throwable, Void> p = deferredManager
				.when(successCallable(100, 1000)).done(new DoneCallback() {
					public void onDone(Object result) {
						Assert.assertEquals(result, 100);
						holder.set((Integer) result);
					}
				}).fail(new FailCallback() {
					public void onFail(Object result) {
						failCount.incrementAndGet();
					}
				});
		
		try {
			synchronized (p) {
				while (p.isPending()) {
					p.wait();
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		holder.assertEquals(100);
		Assert.assertEquals(0, failCount.get());
	}
	
	@Test
	public void testWaitSafely() {
		final ValueHolder<Integer> holder = new ValueHolder<Integer>();
		final AtomicInteger failCount = new AtomicInteger();
		Promise<Integer, Throwable, Void> p = deferredManager
				.when(successCallable(100, 1000)).done(new DoneCallback() {
					public void onDone(Object result) {
						Assert.assertEquals(result, 100);
						holder.set((Integer) result);
					}
				}).fail(new FailCallback() {
					public void onFail(Object result) {
						failCount.incrementAndGet();
					}
				});
		
		try {
			p.waitSafely();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		holder.assertEquals(100);
		Assert.assertEquals(0, failCount.get());
	}
}
