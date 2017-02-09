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
package io.bsoa.rpc.client;

import java.util.Collection;
import java.util.List;

import io.bsoa.rpc.config.ConsumerConfig;
import io.bsoa.rpc.exception.BsoaRpcException;
import io.bsoa.rpc.message.RpcRequest;

/**
 * <p></p>
 *
 * Created by zhangg on 2017/1/8 00:08. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public abstract class AbstractLoadBalancer implements LoadBalancer{

    /**
     * 一些客户端的配置
     */
    protected ConsumerConfig consumerConfig;

    /**
     * 筛选服务端连接
     *
     * @param request
     *         请求
     * @param providerInfos
     *         可用连接
     * @return provider
     */
    public ProviderInfo select(RpcRequest request, List<ProviderInfo> providerInfos) {
        if (providerInfos.size() == 0) {
            throw noAliveProvider(consumerConfig.buildKey(), providerInfos);
        }
        if (providerInfos.size() == 1) {
            return providerInfos.get(0);
        } else {
            return doSelect(request, providerInfos);
        }
    }

    private BsoaRpcException noAliveProvider(String key, Collection<ProviderInfo> providerInfos) {
        // TODO
        return new BsoaRpcException(22222, "No Alive Provider");
    }


    /**
     * 根据负载均衡筛选
     *
     * @param invocation
     *         请求
     * @param providerInfos
     *         全部服务端连接
     * @return 服务端连接 provider
     */

    public abstract ProviderInfo doSelect(RpcRequest invocation, List<ProviderInfo> providerInfos);

    /**
     * Gets consumer config.
     *
     * @return the consumer config
     */
    public ConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    /**
     * Sets consumer config.
     *
     * @param consumerConfig
     *         the consumer config
     */
    public void setConsumerConfig(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    /**
     * Gets weight.
     *
     * @param providerInfo
     *         the provider
     * @return the weight
     */
    protected int getWeight(ProviderInfo providerInfo) {
        // 从provider中或得到相关权重,默认值100
        return providerInfo.getWeight() < 0 ? 0 : providerInfo.getWeight();
    }

    @Override
    public void init(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }


}
