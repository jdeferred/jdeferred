package org.jdeferred.multiple;

/**
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
public interface MultipleResults3<V1, V2, V3> extends MultipleResults {
	OneResult<V1> getFirst();

	OneResult<V2> getSecond();

	OneResult<V3> getThird();
}
