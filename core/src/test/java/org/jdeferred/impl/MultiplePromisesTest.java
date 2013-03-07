/*
 * Copyright 2013 Ray Tsang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.impl;

import java.util.concurrent.Callable;
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
import org.jdeferred.multiple.CombinedPromiseProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneProgress;
import org.jdeferred.multiple.OneReject;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"rawtypes"})
public class MultiplePromisesTest extends AbstractDeferredTest<Integer> {
	@Test
	public void testMultipleDoneWait() {
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
				holder.set(1);
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
	}
	
	@Test
	public void testMultipleAlwaysWait() {
		final AtomicInteger alwaysCounter = new AtomicInteger();
		
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
				holder.set(1);
			}
		}).always(new AlwaysCallback<MultipleResults, OneReject>() {

			@Override
			public void onAlways(State state, MultipleResults results,
					OneReject rejected) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
				alwaysCounter.incrementAndGet();
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
		Assert.assertEquals(1, alwaysCounter.get());
	}
	
	@Test
	public void testMultipleFailWait() {
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
				Assert.fail("Shouldn't be here");
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
				Assert.assertEquals(0, result.getIndex());
				holder.set(1);
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
	}
	
	@Test
	public void testComplex() {
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
			}
		});
		
		deferredManager.when(p1, p2)
		.then(new DoneCallback<MultipleResults>() {
			public void onDone(MultipleResults results) {
				Assert.assertEquals(2, results.size());
				Assert.assertEquals(100, results.get(0).getResult());
				Assert.assertEquals("Hello", results.get(1).getResult());
				holder.set(1);
			}
		}).fail(new FailCallback<OneReject>() {
			public void onFail(OneReject result) {
				Assert.fail("shouldn't be here");
			}
		});
		
		waitForCompletion();
		Assert.assertEquals((Integer) 1, holder.get());
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
			}).progress(new ProgressCallback<CombinedPromiseProgress>() {
				@Override
				public void onProgress(CombinedPromiseProgress progress) {
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
}
