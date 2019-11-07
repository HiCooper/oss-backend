/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.berry.oss.lock.service;

import com.berry.oss.lock.exception.LockExistsException;
import com.berry.oss.lock.exception.LockNotHeldException;
import com.berry.oss.lock.model.Lock;

/**
 * Strategy interface for server side implementations of a global lock. Implementations
 * could be hopefully made for any back end that supports locks with these semantics. A
 * lock is identified by name which is globally unique, i.e. there can be only one holder
 * of the lock at a given time. Locks expire (in a way to be determined by the
 * implementation), and have a String value (usually random) which is used by the lock
 * holder to prove that he holds the lock. The value is thus unique per lock and per
 * expiry period (i.e. 2 locks held at different times with the same name will have
 * different values)..
 *
 * @author Dave Syer
 */
public interface LockService {

    /**
     * Iterate the existing locks.
     *
     * @return an iterable of all locks
     */
    Iterable<Lock> findAll();

    /**
     * Acquire a lock by name. Only one process (globally) should be able to obtain and
     * hold the lock with this name at any given time. Locks expire and can also be
     * released by the owner, so after either of those events the lock can be acquired by
     * the same or a different process.
     *
     * @param name the name identifying the lock
     * @return a Lock containing a value that can be used to release or refresh the lock
     * @throws LockExistsException
     */
    Lock create(String name) throws LockExistsException;

    /**
     * Release a lock before it expires. Only the holder of a lock can release it, and the
     * holder must have the correct unique value to prove that he holds it.
     *
     * @param name  the name of the lock
     * @param value the value of the lock (which has to match the value when it was
     *              acquired)
     * @return true if successful
     * @throws LockNotHeldException
     */
    boolean release(String name, String value) throws LockNotHeldException;

    /**
     * The holder of a lock can refresh it, extending its expiry. If the caller does not
     * hold the lock there will be an exception, but the implementation may not be able to
     * tell if it was because he formerly held the lock and it expired, or if it simply
     * was never held.
     *
     * @param name  the name of the lock
     * @param value the value of the lock (which has to match the value when it was
     *              acquired)
     * @return a new lock with a new value and a new expiry
     * @throws LockNotHeldException if the value does not match the current value or if
     *                              the lock doesn't exist (e.g. if it expired)
     */
    Lock refresh(String name, String value) throws LockNotHeldException;

}
