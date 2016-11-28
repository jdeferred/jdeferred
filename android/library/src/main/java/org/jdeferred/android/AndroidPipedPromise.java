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
package org.jdeferred.android;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

public class AndroidPipedPromise<D, F, P, D_OUT, F_OUT, P_OUT> extends AndroidDeferredObject<D_OUT, F_OUT, P_OUT> implements AndroidPromise<D_OUT, F_OUT, P_OUT> {
	public AndroidPipedPromise(final Promise<D, F, P> promise, final AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter, final AndroidFailPipe<F, D_OUT, F_OUT, P_OUT> failFilter, final AndroidProgressPipe<P, D_OUT, F_OUT, P_OUT> progressFilter) {
		promise.done(new AndroidDoneCallback<D>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return doneFilter!=null ? doneFilter.getExecutionScope() : AndroidExecutionScope.BACKGROUND;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onDone(D result) {
				if (doneFilter != null) pipe(doneFilter.pipeDone(result));
				else AndroidPipedPromise.this.resolve((D_OUT) result);

			}
		}).fail(new AndroidFailCallback<F>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return doneFilter!=null ? doneFilter.getExecutionScope() : AndroidExecutionScope.BACKGROUND;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onFail(F result) {
				if (failFilter != null)  pipe(failFilter.pipeFail(result));
				else AndroidPipedPromise.this.reject((F_OUT) result);
			}
		}).progress(new AndroidProgressCallback<P>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return doneFilter!=null ? doneFilter.getExecutionScope() : AndroidExecutionScope.BACKGROUND;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onProgress(P progress) {
				if (progressFilter != null) pipe(progressFilter.pipeProgress(progress));
				else AndroidPipedPromise.this.notify((P_OUT) progress);
			}
		});
	}
	
	protected Promise<D_OUT, F_OUT, P_OUT> pipe(final Promise<D_OUT, F_OUT, P_OUT> promise) {
		promise.done(new AndroidDoneCallback<D_OUT>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return AndroidExecutionScope.BACKGROUND;
			}

			@Override
			public void onDone(D_OUT result) {
				AndroidPipedPromise.this.resolve(result);
			}
		}).fail(new AndroidFailCallback<F_OUT>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return AndroidExecutionScope.BACKGROUND;
			}

			@Override
			public void onFail(F_OUT result) {
				AndroidPipedPromise.this.reject(result);
			}
		}).progress(new AndroidProgressCallback<P_OUT>() {
			@Override
			public AndroidExecutionScope getExecutionScope() {
				return AndroidExecutionScope.BACKGROUND;
			}

			@Override
			public void onProgress(P_OUT progress) {
				AndroidPipedPromise.this.notify(progress);
			}
		});
		
		return promise;
	}
}