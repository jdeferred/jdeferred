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
package org.jdeferred2;

/**
 * A callback invoked when the {@code Promise} publishes intermediate results while its state is still
 * {@link Promise.State#PENDING}.
 *
 * @param <P> Type used for {@link Deferred#notify(Object)}
 *
 * @author Ray Tsang
 * @see Deferred#notify(Object)
 * @see Promise#progress(ProgressCallback)
 */
public interface ProgressCallback<P> {
	void onProgress(final P progress);
}
