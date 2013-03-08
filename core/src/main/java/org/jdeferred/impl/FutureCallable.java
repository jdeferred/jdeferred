package org.jdeferred.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureCallable<V> implements Callable<V> {
	private final Future<V> future;
	
	public FutureCallable(Future<V> future) {
		this.future = future;
	}

	@Override
	public V call() throws Exception {
		try {
			return future.get();
		} catch (InterruptedException e) {
			throw e;
		} catch (ExecutionException e) {
			if (e.getCause() instanceof Exception)
				throw (Exception) e.getCause();
			else throw e;
		}
	}

}
