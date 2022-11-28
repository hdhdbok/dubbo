/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.threadpool;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

import java.util.concurrent.Executor;

/**
 * ThreadPool
 * 现阶段，框架中默认含有四种线程池扩展的实现，以下内容摘自官方文档：
 * • FixedThreadPool: 固定大小线程池，启动时建立线程，不关闭，一直持有。
 * • CachedThreadPool: 缓存线程池，空闲一分钟自动删除，需要时重建。
 * • LimitedThreadPool: 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
 * • EagerThreadPoolo: 优先创建 Worker 线程池。在任务数量大于 corePoolSize 小于 maximumPoolSize 时，优先创建Worker来处理任务。
 *          当任务数量大于 maximumPoolSize 时，将任务放入阻塞队列。
 *          阻塞队列充满时抛出 RejectedExecutionException (cached 在任务数量超过 maximumPoolSize 时直接抛出异常而不是将任务放入阻塞队列)。
 */
@SPI("fixed")
public interface ThreadPool {

    /**
     * Thread pool
     *
     * @param url URL contains thread parameter
     * @return thread pool
     */
    @Adaptive({Constants.THREADPOOL_KEY})
    Executor getExecutor(URL url);

}