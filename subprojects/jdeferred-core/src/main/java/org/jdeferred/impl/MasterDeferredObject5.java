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
package org.jdeferred.impl;

import org.jdeferred.CallbackExceptionHandler;
import org.jdeferred.Promise;

/***
 * @author Ray Tsang
 * @author Andres Almiray
 */
class MasterDeferredObject5<V1, V2, V3, V4, V5> extends AbstractMasterDeferredObject {
	MasterDeferredObject5(CallbackExceptionHandler callbackExceptionHandler,
	                      Promise<V1, ?, ?> promiseV1,
	                      Promise<V2, ?, ?> promiseV2,
	                      Promise<V3, ?, ?> promiseV3,
	                      Promise<V4, ?, ?> promiseV4,
	                      Promise<V5, ?, ?> promiseV5) {
		super(new MutableMultipleResults5<V1, V2, V3, V4, V5>(), callbackExceptionHandler);
		configurePromise(0, promiseV1);
		configurePromise(1, promiseV2);
		configurePromise(2, promiseV3);
		configurePromise(3, promiseV4);
		configurePromise(4, promiseV5);
	}
}
