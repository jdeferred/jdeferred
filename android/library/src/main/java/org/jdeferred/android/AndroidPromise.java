package org.jdeferred.android;

import org.jdeferred.*;

public interface AndroidPromise<D, F, P> extends Promise<D,F,P> {
    <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter);
    <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
            AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
            AndroidFailPipe<F, D_OUT, F_OUT, P_OUT> failFilter);
    <D_OUT, F_OUT, P_OUT> AndroidPromise<D_OUT, F_OUT, P_OUT> then(
            AndroidDonePipe<D, D_OUT, F_OUT, P_OUT> doneFilter,
            AndroidFailPipe<F, D_OUT, F_OUT, P_OUT> failFilter,
            AndroidProgressPipe<P, D_OUT, F_OUT, P_OUT> progressFilter);

    AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback);
    AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback,
                                 AndroidFailCallback<F> failCallback);
    AndroidPromise<D, F, P> then(AndroidDoneCallback<D> doneCallback,
                                 AndroidFailCallback<F> failCallback, AndroidProgressCallback<P> progressCallback);

    AndroidPromise<D, F, P> done(AndroidDoneCallback<D> callback);
    AndroidPromise<D, F, P> fail(AndroidFailCallback<F> callback);
    AndroidPromise<D, F, P> always(AndroidAlwaysCallback<D, F> callback);

}
