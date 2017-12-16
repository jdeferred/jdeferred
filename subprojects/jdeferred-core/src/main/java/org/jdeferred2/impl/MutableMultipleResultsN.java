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

import org.jdeferred2.multiple.MultipleResultsN;
import org.jdeferred2.multiple.OneResult;

/**
 * Contains multiple instances of {@link OneResult}.
 *
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
class MutableMultipleResultsN<V1, V2, V3, V4, V5> extends AbstractMutableMultipleResults implements MutableMultipleResults, MultipleResultsN<V1, V2, V3, V4, V5> {
	private OneResult<V1> v1;
	private OneResult<V2> v2;
	private OneResult<V3> v3;
	private OneResult<V4> v4;
	private OneResult<V5> v5;

	MutableMultipleResultsN(int size) {
		super(size);
	}

	protected void setFirst(OneResult<V1> v1) {
		super.set(0, v1);
		this.v1 = v1;
	}

	protected void setSecond(OneResult<V2> v2) {
		super.set(1, v2);
		this.v2 = v2;
	}

	protected void setThird(OneResult<V3> v3) {
		super.set(2, v3);
		this.v3 = v3;
	}

	protected void setFourth(OneResult<V4> v4) {
		super.set(3, v4);
		this.v4 = v4;
	}

	protected void setFifth(OneResult<V5> v5) {
		super.set(4, v5);
		this.v5 = v5;
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
	public OneResult<V3> getThird() {
		return v3;
	}

	@Override
	public OneResult<V4> getFourth() {
		return v4;
	}

	@Override
	public OneResult<V5> getFifth() {
		return v5;
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
			case 2:
				this.v3 = (OneResult<V3>) result;
				break;
			case 3:
				this.v4 = (OneResult<V4>) result;
				break;
			case 5:
				this.v5 = (OneResult<V5>) result;
				break;
		}
	}
}
