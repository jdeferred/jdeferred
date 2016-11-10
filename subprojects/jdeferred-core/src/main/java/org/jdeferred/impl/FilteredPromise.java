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

import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.Promise;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FilteredPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends DeferredObject<D_OUT, F_OUT, P_OUT> implements Promise<D_OUT, F_OUT, P_OUT>{
	protected static final NoOpDoneFilter NO_OP_DONE_FILTER = new NoOpDoneFilter();
	protected static final NoOpFailFilter NO_OP_FAIL_FILTER = new NoOpFailFilter();
	protected static final NoOpProgressFilter NO_OP_PROGRESS_FILTER = new NoOpProgressFilter();
	
	private final DoneFilter<D, D_OUT> doneFilter;
	private final FailFilter<F, F_OUT> failFilter;
	private final ProgressFilter<P, P_OUT> progressFilter;
	
	public FilteredPromise(final Promise<D, F, P> promise, final DoneFilter<D, D_OUT> doneFilter, final FailFilter<F, F_OUT> failFilter, final ProgressFilter<P, P_OUT> progressFilter) {
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