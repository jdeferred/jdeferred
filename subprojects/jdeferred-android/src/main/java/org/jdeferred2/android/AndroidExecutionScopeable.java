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

/**
 * This interface allows a callback to specify whether it should run in the UI thread
 * or background thread.
 * 
 * Anonymous class doesn't retain annotations at type nor method level!
 * So rather than annotating class and/or methods, we must use an interface. 
 * 
 * @author Ray Tsang
 *
 */
public interface AndroidExecutionScopeable {
	public AndroidExecutionScope getExecutionScope();
}
