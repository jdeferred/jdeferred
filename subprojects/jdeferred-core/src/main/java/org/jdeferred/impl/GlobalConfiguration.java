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
package org.jdeferred.impl;

import org.jdeferred.ExceptionHandler;
import org.jdeferred.impl.DefaultExceptionHandler;

public final class GlobalConfiguration {
	private static ExceptionHandler globalExceptionHandler = new DefaultExceptionHandler();

	private GlobalConfiguration() {};

	public static void setGlobalExceptionHandler(ExceptionHandler exceptionHandler) {
		if (exceptionHandler == null) {
			throw new IllegalArgumentException("exceptionHandler cannot be null");
		}
		globalExceptionHandler = exceptionHandler;
	}

	public static ExceptionHandler getGlobalExceptionHandler() {
		return globalExceptionHandler;
	}
}
