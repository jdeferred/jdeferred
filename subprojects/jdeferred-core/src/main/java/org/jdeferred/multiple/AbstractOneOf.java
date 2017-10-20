package org.jdeferred.multiple;

/**
 * @author Andres Almiray
 */
abstract class AbstractOneOf<T> implements OneOf<T> {
	protected final int index;

	AbstractOneOf(int index) {
		this.index = index;
	}

	@Override
	public final int getIndex() {
		return index;
	}
}
