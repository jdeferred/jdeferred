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

import org.jdeferred2.multiple.MultipleResults2;
import org.jdeferred2.multiple.OneResult;

/**
 * Contains 2 instances of {@link OneResult}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
class MutableMultipleResults2<V1, V2> extends AbstractMutableMultipleResults implements MutableMultipleResults, MultipleResults2<V1, V2> {
	private OneResult<V1> v1;
	private OneResult<V2> v2;

	MutableMultipleResults2() {
		super(2);
	}

	protected void setFirst(OneResult<V1> v1) {
		super.set(0, v1);
		this.v1 = v1;
	}

	protected void setSecond(OneResult<V2> v2) {
		super.set(1, v2);
		this.v2 = v2;
	}

	@Override
	public OneResult<V1> getFirst() {
		return v1;
	}

	@Override
	public OneResult<V2> getSecond() {
		return v2;
	}

	@Override
	@SuppressWarnings({"unchecked"})
	public void set(int index, OneResult<?> result) {
		super.set(index, result);
		switch (index) {
			case 0:
				this.v1 = (OneResult<V1>) result;
				break;
			case 1:
				this.v2 = (OneResult<V2>) result;
				break;
		}
	}

	@Override
	public final int size() {
		return 2;
	}
}
