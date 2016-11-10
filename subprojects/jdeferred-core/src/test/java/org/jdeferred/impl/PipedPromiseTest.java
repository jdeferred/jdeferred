/*
 * Copyright Ray Tsang ${author}
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

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailPipe;
import org.jdeferred.Promise;
import org.junit.Test;

public class PipedPromiseTest extends AbstractDeferredTest {
	@Test
	public void testDoneRewireFilter() {
		final ValueHolder<Integer> preRewireValue = new ValueHolder<Integer>();
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				return 100;
			}
		};
		
		deferredManager.when(task).then(new DonePipe<Integer, Integer, Void, Void>() {
			@Override
			public Promise<Integer, Void, Void> pipeDone(Integer result) {
				preRewireValue.set(result);
				return new DeferredObject<Integer, Void, Void>().resolve(1000);
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer value) {
				postRewireValue.set(value);
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals(100);
		postRewireValue.assertEquals(1000);
	}
	
	@Test
	public void testFailRewireFilter() {
		final ValueHolder<String> preRewireValue = new ValueHolder<String>();
		final ValueHolder<String> postRewireValue = new ValueHolder<String>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				throw new RuntimeException("oops");
			}
		};
		
		deferredManager.when(task).then(null, new FailPipe<Throwable, Integer, String, Void>() {
			@Override
			public Promise<Integer, String, Void> pipeFail(Throwable result) {
				preRewireValue.set(result.getMessage());
				return new DeferredObject<Integer, String, Void>().reject("ouch");
			}
		}).fail(new FailCallback<String>() {
			@Override
			public void onFail(String result) {
				postRewireValue.set(result);
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals("oops");
		postRewireValue.assertEquals("ouch");
	}
	
	@Test
	public void testNullDoneRewireFilter() {
		final ValueHolder<Boolean> failed = new ValueHolder<Boolean>(false);
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				return 100;
			}
		};
		
		deferredManager.when(task).then(null, new FailPipe<Throwable, Integer, String, Void>() {
			@Override
			public Promise<Integer, String, Void> pipeFail(Throwable result) {
				return new DeferredObject<Integer, String, Void>().reject("ouch");
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				postRewireValue.set(result);
			}
		}).fail(new FailCallback<String>() {
			@Override
			public void onFail(String result) {
				failed.set(true);
			}
		});
		
		waitForCompletion();
		failed.assertEquals(false);
		postRewireValue.assertEquals(100);
	}
	
	@Test
	public void testDoneRewireToFail() {
		final ValueHolder<Integer> preRewireValue = new ValueHolder<Integer>();
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		final ValueHolder<String> failed = new ValueHolder<String>();
		
		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				return 10;
			}
		}).then(new DonePipe<Integer, Integer, String, Void>() {
			@Override
			public Promise<Integer, String, Void> pipeDone(Integer result) {
				preRewireValue.set(result);
				if (result < 100) {
					return new DeferredObject<Integer, String, Void>().reject("less than 100");
				} else {
					return new DeferredObject<Integer, String, Void>().resolve(result);
				}
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				postRewireValue.set(result);
			}
		}).fail(new FailCallback<String>() {
			@Override
			public void onFail(String result) {
				failed.set(result);
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals(10);
		postRewireValue.assertEquals(null);
		failed.assertEquals("less than 100");
	}
}
