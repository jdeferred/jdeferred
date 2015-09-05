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

import org.jdeferred.multiple.MultipleResults;
import org.jdeferred.multiple.OneResult;


/**
 * Contains a list of {@link OneResult}.
 * @author Ray Tsang
 *
 */
public class AndroidMultipleResults extends MultipleResults {
	public AndroidMultipleResults(int size) {
		super(size);
	}

	@Override
	protected void set(int index, OneResult result) {
		super.set(index, result);
	}
}
