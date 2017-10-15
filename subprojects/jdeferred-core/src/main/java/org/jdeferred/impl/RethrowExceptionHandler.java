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

import org.jdeferred.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This  exception handler will rethrow the exception.
 *
 * @author Ray Tsang
 */
public class RethrowExceptionHandler implements ExceptionHandler {
    final protected Logger log = LoggerFactory.getLogger(RethrowExceptionHandler.class);

    @Override
    public void handleException(CallbackType callbackType, Exception e) {
        throw new RuntimeException("exception caught in " + callbackType, e);
    }
}
