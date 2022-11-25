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
package com.alibaba.dubbo.config.spring.extension;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.config.DubboShutdownHook;
import com.alibaba.dubbo.config.spring.util.BeanFactoryUtils;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Set;

/**
 * SpringExtensionFactory
 */
public class SpringExtensionFactory implements ExtensionFactory {
    private static final Logger logger = LoggerFactory.getLogger(SpringExtensionFactory.class);

    // 能自动去重的 Set 保存 Spring 上下文
    private static final Set<ApplicationContext> contexts = new ConcurrentHashSet<ApplicationContext>();

    private static final ApplicationListener shutdownHookListener = new ShutdownHookListener();

    // 此方法有两处调用
    // 在 ReferenceBean 和 ServiceBean 中会调用静态方法保存Spring上下文，
    // 即一个服务被发布或被引用的时候，对应的 Spring 上下文会被保存下来。
    public static void addApplicationContext(ApplicationContext context) {
        // Spring 的上下文引用会在这里保存
        contexts.add(context);
        BeanFactoryUtils.addApplicationListener(context, shutdownHookListener);
    }

    public static void removeApplicationContext(ApplicationContext context) {
        contexts.remove(context);
    }

    public static Set<ApplicationContext> getContexts() {
        return contexts;
    }

    // currently for test purpose
    public static void clearContexts() {
        contexts.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> type, String name) {
        // 遍历所有 Spring 上下文，先根据名字从 Spring 容器中查找扩展类
        for (ApplicationContext context : contexts) {
            if (context.containsBean(name)) {
                Object bean = context.getBean(name);
                if (type.isInstance(bean)) {
                    // 根据 name 找到的话就直接返回
                    return (T) bean;
                }
            }
        }

        logger.warn("No spring extension (bean) named:" + name + ", try to find an extension (bean) of type " + type.getName());

        if (Object.class == type) {
            return null;
        }

        for (ApplicationContext context : contexts) {
            try {
                // 根据类型（type）查找
                return context.getBean(type);
            } catch (NoUniqueBeanDefinitionException multiBeanExe) {
                logger.warn("Find more than 1 spring extensions (beans) of type " + type.getName() + ", will stop auto injection. Please make sure you have specified the concrete parameter type and there's only one extension of that type.");
            } catch (NoSuchBeanDefinitionException noBeanExe) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Error when get spring extension(bean) for type:" + type.getName(), noBeanExe);
                }
            }
        }

        logger.warn("No spring extension (bean) named:" + name + ", type:" + type.getName() + " found, stop get bean.");

        // 根据类型也找不到，只能返回 null 了
        return null;
    }

    /**
     * 容器关闭监听器
     */
    private static class ShutdownHookListener implements ApplicationListener {
        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof ContextClosedEvent) {
                // we call it anyway since dubbo shutdown hook make sure its destroyAll() is re-entrant.
                // 无论如何我们都调用它，因为 dubbo 服务关闭的钩子函数 destroyAll() 是可重入的。
                // pls. note we should not remove dubbo shutdown hook when spring framework is present, this is because
                // its shutdown hook may not be installed.
                // 注意，当 spring 框架存在时，我们不应该删除 dubbo 的关闭钩子，这是因为 dubbo 的关闭钩子可能没有安装。
                DubboShutdownHook shutdownHook = DubboShutdownHook.getDubboShutdownHook();
                shutdownHook.destroyAll();
            }
        }
    }

}
