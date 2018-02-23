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
 * Handles global exceptions that are uncaught by the callbacks.
 *
 * @author Ray Tsang
 * @since 2.0
 */
public interface CallbackExceptionHandler {
	enum CallbackType {
		DONE_CALLBACK,
		FAIL_CALLBACK,
		PROGRESS_CALLBACK,
		ALWAYS_CALLBACK
	}

	void handleException(CallbackType callbackType, Exception e);
}
