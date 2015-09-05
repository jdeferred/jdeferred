package org.jdeferred.android.test;

import junit.framework.Assert;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.*;

import android.test.AndroidTestCase;
import org.jdeferred.android.annotation.ExecutionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AndroidDeferredManagerTest extends AndroidTestCase {
	public void testDeferredAsyncTask() {
		AndroidDeferredManager dm = new AndroidDeferredManager();

		final ValueHolder<String> backgroundThreadGroupName = new ValueHolder<String>();
		final ValueHolder<String> doneThreadGroupName = new ValueHolder<String>();

		try {
			dm.when(new DeferredAsyncTask<Void, Integer, String>() {
				@Override
				protected String doInBackgroundSafe(Void... nil)
						throws Exception {
					backgroundThreadGroupName.set(Thread.currentThread()
							.getThreadGroup().getName());
					return "Done";
				}
			}).done(new DoneCallback<String>() {

				@Override
				public void onDone(String result) {
					doneThreadGroupName.set(Thread.currentThread()
							.getThreadGroup().getName());
				}

			}).waitSafely();
		} catch (InterruptedException e) {
			// Do nothing
		}

		doneThreadGroupName.assertEquals("main");
		Assert.assertFalse(
				String.format(
						"Background Thread Group [%s] shouldn't be the same as Thread Group in Done [%s]",
						backgroundThreadGroupName.get(),
						doneThreadGroupName.get()), backgroundThreadGroupName
						.equals(doneThreadGroupName));
	}

	public void testPipedPromise() {
		AndroidDeferredManager dm = new AndroidDeferredManager();

		final ExecutionScopeTester executionScopeTester = new ExecutionScopeTester();

		final List<AndroidExecutionScope> scopes = new ArrayList<>();
		final List<String> outputs = new ArrayList<>();

		try {
			dm.when(new Callable<String>() {
				@Override
				public String call() throws Exception {
					return "string1";
				}
			}).then(new AndroidDonePipe<String, String, Throwable, Void>() {

				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.BACKGROUND;
				}

				@Override
				public AndroidPromise<String, Throwable, Void> pipeDone(String result) {
					scopes.add(executionScopeTester.determineExecutionScopeForObject(this));
					outputs.add(result);
					return new AndroidDeferredObject<String, Throwable, Void>().resolve("string2");
				}
			})
			.then(new AndroidDonePipe<String, String, Throwable, Void>() {
				@Override
				public AndroidPromise<String, Throwable, Void> pipeDone(String result) {
					scopes.add(executionScopeTester.determineExecutionScopeForObject(this));
					outputs.add(result);
					return new AndroidDeferredObject<String, Throwable, Void>().resolve("string3");
				}

				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.UI;
				}
			})
			.done(new AndroidDoneCallback<String>() {
				@Override
				public AndroidExecutionScope getExecutionScope() {
					return AndroidExecutionScope.BACKGROUND;
				}

				@Override
				public void onDone(String result) {
					scopes.add(executionScopeTester.determineExecutionScopeForObject(this));

					outputs.add(result);
				}
			})
			.waitSafely();
		} catch (InterruptedException e) {
			// Do nothing
		}

		Assert.assertEquals("string1", outputs.get(0));
		Assert.assertEquals("string2", outputs.get(1));
		Assert.assertEquals("string3", outputs.get(2));

		Assert.assertEquals(AndroidExecutionScope.BACKGROUND, scopes.get(0));
		Assert.assertEquals(AndroidExecutionScope.UI, scopes.get(1));
		Assert.assertEquals(AndroidExecutionScope.BACKGROUND, outputs.get(2));
	}

	private class ExecutionScopeTester extends AndroidDeferredObject {
		public AndroidExecutionScope determineExecutionScopeForObject(Object object) {
			return this.determineAndroidExecutionScope(object);
		}
	}
}
