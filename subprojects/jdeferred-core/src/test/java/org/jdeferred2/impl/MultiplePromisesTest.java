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

import org.jdeferred2.AlwaysCallback;
import org.jdeferred2.DeferredCallable;
import org.jdeferred2.DeferredFutureTask;
import org.jdeferred2.DeferredRunnable;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.Promise.State;
import org.jdeferred2.multiple.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"rawtypes"})
public class MultiplePromisesTest extends AbstractDeferredTest {
	@Test
	public void testMultipleDoneWait() {
		final AtomicInteger doneCount = new AtomicInteger();

		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return 100;
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults2<Integer, String>>() {
			public void onDone(MultipleResults2<Integer, String> results) {
				assertMultipleResults(results);
				doneCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
	}

	@Test
	public void testMultipleAlwaysWait() {
		final AtomicInteger doneCount = new AtomicInteger();
		final AtomicInteger alwaysCount = new AtomicInteger();

		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return 100;
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults2<Integer, String>>() {
			public void onDone(MultipleResults2<Integer, String> results) {
				assertMultipleResults(results);
				doneCount.incrementAndGet();
			}
		}).always(new AlwaysCallback<MultipleResults2<Integer, String>, OneReject<Throwable>>() {
			@Override
			public void onAlways(State state, MultipleResults2<Integer, String> results,
			                     OneReject<Throwable> rejected) {
				assertMultipleResults(results);
				alwaysCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
		Assert.assertEquals(1, alwaysCount.get());
	}

	@Test
	public void testMultipleFailWait() {
		final AtomicInteger failCount = new AtomicInteger();
		final AtomicInteger doneCount = new AtomicInteger();

		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				throw new RuntimeException("oops");
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults2<Integer, String>>() {
			public void onDone(MultipleResults2<Integer, String> results) {
				doneCount.incrementAndGet();
			}
		}).fail(new FailCallback<OneReject<Throwable>>() {
			public void onFail(OneReject<Throwable> result) {
				Assert.assertEquals(0, result.getIndex());
				failCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(0, doneCount.get());
		Assert.assertEquals(1, failCount.get());
	}

	@Test
	public void testComplex() {
		final AtomicInteger failCount = new AtomicInteger();
		final AtomicInteger doneCount = new AtomicInteger();

		Promise<Integer, Throwable, Void> p1 = deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return 100;
			}
		}).then(new DoneCallback<Integer>() {
			public void onDone(Integer result) {
				Assert.assertEquals((Integer) 100, result);
				doneCount.incrementAndGet();
			}
		});
		Promise<String, Throwable, Void> p2 = deferredManager.when(new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<String>() {
			public void onDone(String result) {
				Assert.assertEquals("Hello", result);
				doneCount.incrementAndGet();
			}
		});

		Promise<MultipleResults2<Integer, String>, OneReject<Throwable>, MasterProgress> px = deferredManager.when(p1, p2);
		px.fail(new FailCallback<OneReject<Throwable>>() {
			@Override
			public void onFail(OneReject<Throwable> result) {

			}
		});

		deferredManager.when(p1, p2)
			.then(new DoneCallback<MultipleResults2<Integer, String>>() {
				public void onDone(MultipleResults2<Integer, String> results) {
					assertMultipleResults(results);
					doneCount.incrementAndGet();
				}
			}).fail(new FailCallback<OneReject<Object>>() {
			public void onFail(OneReject<Object> result) {
				failCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(3, doneCount.get());
		Assert.assertEquals(0, failCount.get());
	}

	@Test
	public void testPorgressWait() {
		DeferredCallable<Integer, Integer> task1 = new DeferredCallable<Integer, Integer>() {
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

		DeferredRunnable<String> task2 = new DeferredRunnable<String>() {

			@Override
			public void run() {
				for (int i = 1; i <= 3; i++) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
					}
					notify("R-" + i);
				}
			}
		};

		final AtomicInteger alwaysCounter = new AtomicInteger();
		final AtomicInteger doneCounter = new AtomicInteger();
		final AtomicInteger task1ProgressCounter = new AtomicInteger();
		final AtomicInteger task2ProgressCounter = new AtomicInteger();
		final AtomicInteger combinedProgressCounter = new AtomicInteger();

		deferredManager
			.when(
				new DeferredFutureTask<Integer, Integer>(task1),
				new DeferredFutureTask<Void, String>(task2))
			.done(new DoneCallback<MultipleResults2<Integer, Void>>() {
				public void onDone(MultipleResults2<Integer, Void> results) {
					Assert.assertEquals(2, results.size());
					Assert.assertEquals(55, results.get(0).getResult());
					Assert.assertEquals(null, results.get(1).getResult());
					Assert.assertEquals(55, (int) results.getFirst().getResult());
					Assert.assertEquals(null, results.getSecond().getResult());
					doneCounter.incrementAndGet();
				}
			}).fail(new FailCallback<OneReject<Throwable>>() {
			public void onFail(OneReject result) {
				Assert.fail("Shouldn't be here");
			}
		}).progress(new ProgressCallback<MasterProgress>() {
			@Override
			public void onProgress(MasterProgress progress) {
				if (progress instanceof OneProgress) {
					OneProgress oneProgress = (OneProgress) progress;
					if (oneProgress.getIndex() == 0)
						task1ProgressCounter.incrementAndGet();
					else if (oneProgress.getIndex() == 1)
						task2ProgressCounter.incrementAndGet();
					else
						Assert.fail("shouldn't be here");
				} else {
					Assert.assertEquals(2, progress.getTotal());
					combinedProgressCounter.incrementAndGet();
				}
			}
		}).always(new AlwaysCallback<MultipleResults2<Integer, Void>, OneReject<Throwable>>() {
			@Override
			public void onAlways(State state, MultipleResults2<Integer, Void> results,
			                     OneReject<Throwable> rejected) {
				Assert.assertEquals(State.RESOLVED, state);
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(55, results.get(0).getResult());
				Assert.assertEquals(null, results.get(1).getResult());
				Assert.assertEquals(55, (int) results.getFirst().getResult());
				Assert.assertEquals(null, results.getSecond().getResult());
				alwaysCounter.incrementAndGet();
			}
		});

		waitForCompletion();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		Assert.assertEquals(1, alwaysCounter.get());
		Assert.assertEquals(1, doneCounter.get());
		Assert.assertEquals(2, combinedProgressCounter.get());
		Assert.assertEquals(10, task1ProgressCounter.get());
		Assert.assertEquals(3, task2ProgressCounter.get());
	}

	@Test
	public void testFutures() {
		final Callable<Integer> callable1 = successCallable(999, 100);
		final Callable<String> callable2 = successCallable("HI", 1000);

		ExecutorService es = deferredManager.getExecutorService();
		Future<Integer> future1 = es.submit(callable1);
		Future<String> future2 = es.submit(callable2);
		final AtomicInteger doneCount = new AtomicInteger();
		deferredManager.when(future1, future2).done(new DoneCallback<MultipleResults2<Integer, String>>() {
			@Override
			public void onDone(MultipleResults2<Integer, String> result) {
				Assert.assertEquals(2, result.size());
				Assert.assertEquals(999, result.get(0).getResult());
				Assert.assertEquals("HI", result.get(1).getResult());
				Assert.assertEquals(999, (int) result.getFirst().getResult());
				Assert.assertEquals("HI", result.getSecond().getResult());
				doneCount.incrementAndGet();
			}
		});

		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
	}

	@Test
	public void testMultipleWait() {
		final AtomicInteger doneCount = new AtomicInteger();

		Promise<MultipleResults2<Integer, String>, OneReject<Throwable>, MasterProgress> p = deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return 100;
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults2<Integer, String>>() {
			public void onDone(MultipleResults2<Integer, String> results) {
				assertMultipleResults(results);
				doneCount.incrementAndGet();
			}
		});

		synchronized (p) {
			try {
				while (p.isPending()) {
					p.wait();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		Assert.assertEquals(1, doneCount.get());
	}

	@Test
	public void testIterable() {
		final AtomicReference<MultipleResults> results = new AtomicReference<MultipleResults>();
		List actions = new LinkedList();

		actions.add(new DeferredCallable<String, Integer>() {
			@Override
			public String call() throws Exception {
			    return "r1";
			}
		});

		actions.add(successCallable("r2", 100));

		actions.add(new DeferredObject<String, Void, Void>().resolve("r3").promise());

		Promise<MultipleResults, OneReject<?>, MasterProgress> p = deferredManager.when(actions);
		p.done(new DoneCallback<MultipleResults>() {
			@Override
			public void onDone(MultipleResults result) {
			    results.set(result);
			}
		});

		try {
			p.waitSafely();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		Set<String> resultSet = new LinkedHashSet<String>();

		for (OneResult<?> r : results.get()) {
			resultSet.add(r.getResult().toString());
		}
		Assert.assertEquals(3, resultSet.size());

		Assert.assertTrue("r1 is not in result", resultSet.contains("r1"));
		Assert.assertTrue("r2 is not in result", resultSet.contains("r2"));
		Assert.assertTrue("r3 is not in result", resultSet.contains("r3"));
	}

	@Test
	public void testFailedIterable() {
	    boolean caughtIAE = false;
		final AtomicReference<MultipleResults> results = new AtomicReference<MultipleResults>();
		List actions = new LinkedList();
		Set<String> resultSet = new LinkedHashSet<String>();

		actions.add(new DeferredCallable<String, Integer>() {
			@Override
			public String call() throws Exception {
			    return "r1";
			}
		});

		actions.add("r2");

		actions.add(successCallable("r3", 100));

		try {
			Promise<MultipleResults, OneReject<?>, MasterProgress> p = deferredManager.when(actions);
			p.done(new DoneCallback<MultipleResults>() {
				@Override
				public void onDone(MultipleResults result) {
					results.set(result);
				}
			});

			try {
				p.waitSafely();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} catch (IllegalArgumentException e) {
		    caughtIAE = true;
		}

		Assert.assertNull("Expecting no results", results.get());
		Assert.assertTrue("IllegalArgumentException was not caught", caughtIAE);
	}

	@Test
	public void testMultipleWaitSafely() {
		final AtomicInteger doneCount = new AtomicInteger();

		Promise<MultipleResults2<Integer, String>, OneReject<Throwable>, MasterProgress> p = deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}

				return 100;
			}
		}, new Callable<String>() {
			public String call() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				return "Hello";
			}
		}).then(new DoneCallback<MultipleResults2<Integer, String>>() {
			public void onDone(MultipleResults2<Integer, String> results) {
				assertMultipleResults(results);
				doneCount.incrementAndGet();
			}
		});

		try {
			p.waitSafely();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Assert.assertEquals(1, doneCount.get());
	}

	private void assertMultipleResults(MultipleResults2<Integer, String> results) {
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(100, results.get(0).getResult());
		Assert.assertEquals("Hello", results.get(1).getResult());
		Assert.assertEquals(100, (int) results.getFirst().getResult()); // ambiguous call without cast!
		Assert.assertEquals("Hello", results.getSecond().getResult());
	}
}
