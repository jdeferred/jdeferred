package org.jdeferred.multiple;

/**
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
public interface MultipleResultsN<V1, V2, V3, V4, V5> extends MultipleResults {
	OneResult<V1> getFirst();

	OneResult<V2> getSecond();

	OneResult<V3> getThird();

	OneResult<V4> getFourth();

	OneResult<V5> getFifth();
}
