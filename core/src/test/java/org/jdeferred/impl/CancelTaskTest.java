package org.jdeferred.impl;

import org.jdeferred.DeferredFutureTask;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;

public class CancelTaskTest extends AbstractDeferredTest {

	@Test
	public void testCancelTask() {
		DeferredFutureTask<String, Void> deferredFutureTask =
				new DeferredFutureTask<String, Void>(new Callable<String>() {
					@Override
					public String call() throws Exception {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}

						return "Hello";
					}
				});

		Promise<String, Throwable, Void> promise = deferredManager.when(deferredFutureTask);

		deferredFutureTask.cancel(false);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		promise.then(new DoneCallback<String>() {
			@Override
			public void onDone(String result) {
				Assert.fail("Shouldn't be called, because task was cancelled");
			}
		});

		Assert.assertTrue(deferredFutureTask.isCancelled());
	}
}
