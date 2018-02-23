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

import org.jdeferred2.DoneCallback;
import org.jdeferred2.DoneFilter;
import org.jdeferred2.FailCallback;
import org.jdeferred2.FailFilter;
import org.jdeferred2.ProgressCallback;
import org.jdeferred2.ProgressFilter;
import org.jdeferred2.Promise;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends DeferredObject<D_OUT, F_OUT, P_OUT> implements Promise<D_OUT, F_OUT, P_OUT>{
	protected static final NoOpDoneFilter NO_OP_DONE_FILTER = new NoOpDoneFilter();
	protected static final NoOpFailFilter NO_OP_FAIL_FILTER = new NoOpFailFilter();
	protected static final NoOpProgressFilter NO_OP_PROGRESS_FILTER = new NoOpProgressFilter();
	
	private final DoneFilter<D, D_OUT> doneFilter;
	private final FailFilter<F, F_OUT> failFilter;
	private final ProgressFilter<P, P_OUT> progressFilter;
	
	public FilteredPromise(final Promise<D, F, P> promise,
						   final DoneFilter<? super D, ? extends D_OUT> doneFilter,
						   final FailFilter<? super F, ? extends F_OUT> failFilter,
						   final ProgressFilter<? super P, ? extends P_OUT> progressFilter) {
		this.doneFilter = doneFilter == null ? NO_OP_DONE_FILTER : doneFilter;
		this.failFilter = failFilter == null ? NO_OP_FAIL_FILTER : failFilter;
		this.progressFilter = progressFilter == null ? NO_OP_PROGRESS_FILTER : progressFilter;
		
		promise.done(new DoneCallback<D>() {
			@Override
			public void onDone(D result) {
				FilteredPromise.this.resolve(FilteredPromise.this.doneFilter.filterDone(result));
			}
		}).fail(new FailCallback<F>() {

			@Override
			public void onFail(F result) {
				FilteredPromise.this.reject(FilteredPromise.this.failFilter.filterFail(result));
			}
		}).progress(new ProgressCallback<P>() {

			@Override
			public void onProgress(P progress) {
				FilteredPromise.this.notify(FilteredPromise.this.progressFilter.filterProgress(progress));
			}
		});
	}
	
	public static final class NoOpDoneFilter<D> implements DoneFilter<D, D> {
		@Override
		public D filterDone(D result) {
			return result;
		}
	}
	
	public static final class NoOpFailFilter<F> implements FailFilter<F, F> {
		@Override
		public F filterFail(F result) {
			return result;
		}
	}
	
	public static final class NoOpProgressFilter<P> implements ProgressFilter<P, P> {
		@Override
		public P filterProgress(P progress) {
			return progress;
		}
	}
}
