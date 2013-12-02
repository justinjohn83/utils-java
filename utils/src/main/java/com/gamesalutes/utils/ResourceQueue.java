/*
 * Copyright (c) 2013 Game Salutes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Game Salutes - Repackaging and modifications of original work under University of Chicago and Apache License 2.0 shown below
 * 
 * Repackaging from edu.uchicago.nsit.iteco.utils to com.gamesalutes.utils
 * 
 * Copyright 2008 - 2011 University of Chicago
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
 ******************************************************************************/
/* Copyright 2008 - 2010 University of Chicago
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gamesalutes.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author jmontgomery
 */
public final class ResourceQueue<T> implements Disposable
{
    private final Lock lock;
    private final List<T> available;
    private final List<T> used;
    private Map<T,int[]> usedCount;
    private final int totalCount;

    private static final int SLEEP = 20;

    public ResourceQueue(Collection<T> resources)
    {
        if(resources == null)
            throw new NullPointerException("resources");

        this.lock = new ReentrantLock();
        this.available = new ArrayList<T>(resources);
        this.available.removeAll(Arrays.<T>asList((T)null));
        this.used = new ArrayList<T>(this.available.size());

        this.totalCount = this.available.size();

        usedCount = CollectionUtils.createHashMap(available.size(), CollectionUtils.LOAD_FACTOR);
        for(T resource : available)
        {
            usedCount.put(resource, new int[1]);
        }
    }

    public void dispose()
    {
        lock.lock();
        try
        {
            disposeResources(available);
            disposeResources(used);
        }
        finally
        {
            lock.unlock();
        }
    }

    private void disposeResources(List<T> resources)
    {
        for(T e : resources)
        {
            if(e instanceof Disposable)
            {
                try
                {
                    ((Disposable)e).dispose();
                }
                catch(Exception ex) {}
            }
        }
        resources.clear();
    }


    public T get()
            throws InterruptedException
    {
        T resource = null;
        while(resource == null)
        {
            lock.lock();
            try
            {
                resource = obtain();
                if(resource != null)
                    break;
            }
            finally
            {
                lock.unlock();
            }

            Thread.sleep(SLEEP);

        }

        return resource;
    }

    public boolean release(T resource)
    {
        if(resource == null) return false;

        lock.lock();
        try
        {
            if(used.contains(resource))
            {
                used.remove(resource);
                available.add(resource);
                return true;
            }
            return false;
        }
        finally
        {
            lock.unlock();
        }
    }
    private T obtain()
    {
        if(!available.isEmpty())
        {
            T resource = available.remove(available.size()-1);
            used.add(resource);
            ++usedCount.get(resource)[0];
            return resource;
        }

        return null;
    }

    public int getUsedCount(T resource)
    {
        lock.lock();
        try
        {
            int [] count = usedCount.get(resource);
            if(count == null) return -1;
            return count[0];
        }
        finally
        {
            lock.unlock();
        }
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getUsedCount()
    {
        lock.lock();
        try
        {
            return used.size();
        }
        finally
        {
            lock.unlock();
        }
    }
    public int getAvailableCount()
    {
        lock.lock();
        try
        {
            return available.size();
        }
        finally
        {
            lock.unlock();
        }
    }
}
