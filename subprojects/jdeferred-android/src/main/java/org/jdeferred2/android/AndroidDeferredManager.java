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
package org.jdeferred2.android;

import org.jdeferred2.impl.DefaultAndroidDeferredManager;

import java.util.concurrent.ExecutorService;

/**
 * This DeferredManager is designed to execute deferred tasks in the background,
 * but also executes callbacks (e.g., done, fail, progress, and always) in the UI thread.
 * This is important because only UI thread executions can update UI elements!
 * <p>
 * You can use {@link DeferredAsyncTask} to write in the more familiar Android {@link AsyncTask} API
 * and still being able to take advantage of {@link Promise} chaining.
 * <p>
 * Even more powerful, you can also use {@link Promise}, {@link Runnable}, {@link Callable},
 * and any other types supported by {@link DeferredManager}.  This implementation will hand off
 * callbacks to UI thread automatically.
 *
 * @author Ray Tsang
 */
public class AndroidDeferredManager extends DefaultAndroidDeferredManager {
	public AndroidDeferredManager() {
		super();
	}

	public AndroidDeferredManager(ExecutorService executorService) {
		super(executorService);
	}
}
