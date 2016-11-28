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

import org.jdeferred.FailFilter;
import org.jdeferred.Promise;

/**
 * @see Promise#then(AndroidProgressPipe, FailFilter)
 * @author Ray Tsang
 *
 * @param <P> Type of the input
 * @param <P_OUT> Type of the output from this filter
 */
public interface AndroidProgressPipe<P, D_OUT, F_OUT, P_OUT> extends AndroidExecutionScopeable {
	public Promise<D_OUT, F_OUT, P_OUT> pipeProgress(final P result);
}
