package org.jdeferred.android.test;

import junit.framework.Assert;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import android.test.AndroidTestCase;

public class AndroidDeferredManagerTest extends AndroidTestCase {
	protected AndroidDeferredManager dm = new AndroidDeferredManager();

	public void testDeferredAsyncTask() {
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
}
