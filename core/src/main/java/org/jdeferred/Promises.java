package org.jdeferred;

import org.jdeferred.impl.DeferredObject;

/**
 * Created by nwertzberger on 1/12/16.
 */
public class Promises {
    public static <D, F, P> Promise<D, F, P> resolved(D resolve) {
        return new DeferredObject<D, F, P>().resolve(resolve);
    }

    public static <D, F, P> Promise<D, F, P> rejected(F rejection) {
        return new DeferredObject<D, F, P>().reject(rejection);
    }

    public static <D, F, P> Promise<D, F, P> progressed(P progress) {
        return new DeferredObject<D, F, P>().notify(progress);
    }
}
