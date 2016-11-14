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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DeferredCallable;
import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.Promise.State;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneProgress;
import org.jdeferred.multiple.OneReject;
import org.junit.Assert;
import org.junit.Test;

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
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
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
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
				doneCount.incrementAndGet();
			}
		}).always(new AlwaysCallback<MultipleResults, OneReject>() {

			@Override
			public void onAlways(State state, MultipleResults results,
					OneReject rejected) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
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
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				doneCount.incrementAndGet();
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
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
		
		Promise p1 = deferredManager.when(new Callable<Integer>() {
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
		Promise p2 = deferredManager.when(new Callable<String>() {
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
		
		deferredManager.when(p1, p2)
		.then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
				doneCount.incrementAndGet();
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
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
			.done(new DoneCallback<MultipleResults>() {
				public void onDone(MultipleResults results) {
					Assert.assertEquals(2, results.size());
					Assert.assertEquals(55, results.get(0).getResult());
					Assert.assertEquals(null, results.get(1).getResult());
					doneCounter.incrementAndGet();
				}
			}).fail(new FailCallback<OneReject>() {
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
			}).always(new AlwaysCallback<MultipleResults, OneReject>() {
				@Override
				public void onAlways(State state, MultipleResults results,
						OneReject rejected) {
					Assert.assertEquals(State.RESOLVED, state);
					Assert.assertEquals(2, results.size());
					Assert.assertEquals(55, results.get(0).getResult());
					Assert.assertEquals(null, results.get(1).getResult());
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
		deferredManager.when(future1, future2).done(new DoneCallback<MultipleResults>() {
			@Override
			public void onDone(MultipleResults result) {
				Assert.assertEquals(2, result.size());
				Assert.assertEquals(999, result.get(0).getResult());
				Assert.assertEquals("HI", result.get(1).getResult());
				doneCount.incrementAndGet();
			}
		});
		
		waitForCompletion();
		Assert.assertEquals(1, doneCount.get());
	}
	
	@Test
	public void testMultipleWait() {
		final AtomicInteger doneCount = new AtomicInteger();
		
		Promise<MultipleResults, OneReject, MasterProgress> p = deferredManager.when(new Callable<Integer>() {
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
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
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
	public void testMultipleWaitSafely() {
		final AtomicInteger doneCount = new AtomicInteger();
		
		Promise<MultipleResults, OneReject, MasterProgress> p = deferredManager.when(new Callable<Integer>() {
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
		}).then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
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
}
