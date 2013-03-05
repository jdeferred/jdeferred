package org.jdeferred.impl;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;

/**
 * Delegates all operations to {@link #deferred} object.
 * 
 * @author Ray Tsang
 *
 * @param <D>
 * @param <F>
 * @param <P>
 */
public class DeferredProxy<D, F, P> implements Deferred<D, F, P> {
	protected final Deferred<D, F, P> deferred;
	
	public DeferredProxy() {
		this.deferred = new DeferredObject<D, F, P>();
	}
	
	public DeferredProxy(final Deferred<D, F, P> deferred) {
		this.deferred = deferred;
	}

	public Deferred<D, F, P> resolve(D resolve) {
		return deferred.resolve(resolve);
	}

	public org.jdeferred.Promise.State state() {
		return deferred.state();
	}

	public Deferred<D, F, P> notify(P progress) {
		return deferred.notify(progress);
	}

	public boolean isPending() {
		return deferred.isPending();
	}

	public Deferred<D, F, P> reject(F reject) {
		return deferred.reject(reject);
	}

	public boolean isResolved() {
		return deferred.isResolved();
	}

	public boolean isRejected() {
		return deferred.isRejected();
	}

	public Promise<D, F, P> promise() {
		return deferred.promise();
	}

	public Promise<D, F, P> then(DoneCallback<D> doneCallback) {
		return deferred.then(doneCallback);
	}

	public Promise<D, F, P> then(DoneCallback<D> doneCallback,
			FailCallback<F> failCallback) {
		return deferred.then(doneCallback, failCallback);
	}

	public Promise<D, F, P> then(DoneCallback<D> doneCallback,
			FailCallback<F> failCallback, ProgressCallback<P> progressCallback) {
		return deferred.then(doneCallback, failCallback, progressCallback);
	}

	public Promise<D, F, P> done(DoneCallback<D> callback) {
		return deferred.done(callback);
	}

	public Promise<D, F, P> fail(FailCallback<F> callback) {
		return deferred.fail(callback);
	}

	public Promise<D, F, P> always(AlwaysCallback<D, F> callback) {
		return deferred.always(callback);
	}

	public Promise<D, F, P> progress(ProgressCallback<P> callback) {
		return deferred.progress(callback);
	}

	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter) {
		return deferred.then(doneFilter);
	}

	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter) {
		return deferred.then(doneFilter, failFilter);
	}

	public <D_OUT, F_OUT, P_OUT> Promise<D_OUT, F_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter<F, F_OUT> failFilter,
			ProgressFilter<P, P_OUT> progressFilter) {
		return deferred.then(doneFilter, failFilter, progressFilter);
	}
	
	
}
