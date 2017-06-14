package org.jdeferred.multiple;

/**
 * @author Ray Tsang
 * @author Andres Almiray
 * @author Domen
 */
public interface MultipleResults2<V1, V2> extends MultipleResults {
	OneResult<V1> getFirst();

	OneResult<V2> getSecond();
}
