/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bsoa.rpc.bootstrap;

import io.bsoa.rpc.config.ConsumerConfig;
import io.bsoa.rpc.config.ProviderConfig;
import io.bsoa.rpc.ext.ExtensionLoader;
import io.bsoa.rpc.ext.ExtensionLoaderFactory;

import static io.bsoa.rpc.common.BsoaConfigs.DEFAULT_CONSUMER_BOOTSTRAP;
import static io.bsoa.rpc.common.BsoaConfigs.DEFAULT_PROVIDER_BOOTSTRAP;
import static io.bsoa.rpc.common.BsoaConfigs.getStringValue;

/**
 * <p>辅助工具类，更方便的使用发布方法</p>
 * <p>
 * Created by zhangg on 2017/2/8 19:46. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public class Bootstraps {

    /**
     * ServiceExporter扩展加载器
     */
    private final static ExtensionLoader<ServiceExporter> EXPORTER_EXTENSION_LOADER
            = ExtensionLoaderFactory.getExtensionLoader(ServiceExporter.class);
    /**
     * ServiceReferencer扩展加载器
     */
    private final static ExtensionLoader<ServiceReferencer> REFERENCER_EXTENSION_LOADER
            = ExtensionLoaderFactory.getExtensionLoader(ServiceReferencer.class);

    /**
     * 发布一个服务
     *
     * @param providerConfig 服务发布者配置
     * @param <T>            接口类型
     * @return 发布启动类
     */
    public static <T> AbstractProviderBootstrap<T> export(ProviderConfig<T> providerConfig) {
        ServiceExporter exporter = EXPORTER_EXTENSION_LOADER.getExtension(
                getStringValue(DEFAULT_PROVIDER_BOOTSTRAP));
        return exporter.export(providerConfig);
    }

    /**
     * 引用一个服务
     *
     * @param consumerConfig 服务消费者配置
     * @param <T>            接口类型
     * @return 引用启动类
     */
    public static <T> AbstractConsumerBootstrap<T> reference(ConsumerConfig<T> consumerConfig) {
        ServiceReferencer referencer = REFERENCER_EXTENSION_LOADER.getExtension(
                getStringValue(DEFAULT_CONSUMER_BOOTSTRAP));
        return referencer.refer(consumerConfig);
    }
}
