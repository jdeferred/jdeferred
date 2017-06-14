package org.jdeferred.multiple;

/**
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
public interface MultipleResults4<V1, V2, V3, V4> extends MultipleResults {
	OneResult<V1> getFirst();

	OneResult<V2> getSecond();

	OneResult<V3> getThird();

	OneResult<V4> getFourth();
}
