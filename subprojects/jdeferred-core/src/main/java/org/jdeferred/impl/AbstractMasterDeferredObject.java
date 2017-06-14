package org.jdeferred.impl;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.ProgressCallback;
import org.jdeferred.Promise;
import org.jdeferred.multiple.MasterProgress;
import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneProgress;
import org.jdeferred.multiple.OneReject;
import org.jdeferred.multiple.OneResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andres Almiray
 */
class AbstractMasterDeferredObject extends DeferredObject<MultipleResults, OneReject<?>, MasterProgress> implements Promise<MultipleResults, OneReject<?>, MasterProgress> {
	private final MutableMultipleResults results;
	private final int numberOfPromises;
	private final AtomicInteger doneCount = new AtomicInteger();
	private final AtomicInteger failCount = new AtomicInteger();

	AbstractMasterDeferredObject(MutableMultipleResults results) {
		this.results = results;
		this.numberOfPromises = results.size();
	}

	protected <D, F, P> void configurePromise(final int index, final Promise<D, F, P> promise) {
		promise.fail(new FailCallback<F>() {
			public void onFail(F result) {
				synchronized (AbstractMasterDeferredObject.this) {
					if (!AbstractMasterDeferredObject.this.isPending())
						return;

					final int fail = failCount.incrementAndGet();
					AbstractMasterDeferredObject.this.notify(new MasterProgress(
						doneCount.get(),
						fail,
						numberOfPromises));

					AbstractMasterDeferredObject.this.reject(new OneReject<F>(index, promise, result));
				}
			}
		}).progress(new ProgressCallback<P>() {
			public void onProgress(P progress) {
				synchronized (AbstractMasterDeferredObject.this) {
					if (!AbstractMasterDeferredObject.this.isPending())
						return;

					AbstractMasterDeferredObject.this.notify(new OneProgress<P>(
						doneCount.get(),
						failCount.get(),
						numberOfPromises, index, promise, progress));
				}
			}
		}).done(new DoneCallback<D>() {
			public void onDone(D result) {
				synchronized (AbstractMasterDeferredObject.this) {
					if (!AbstractMasterDeferredObject.this.isPending())
						return;

					results.set(index, new OneResult<D>(index, promise, result));
					int done = doneCount.incrementAndGet();

					AbstractMasterDeferredObject.this.notify(new MasterProgress(
						done,
						failCount.get(),
						numberOfPromises));

					if (done == numberOfPromises) {
						AbstractMasterDeferredObject.this.resolve(results);
					}
				}
			}
		});
	}
}
