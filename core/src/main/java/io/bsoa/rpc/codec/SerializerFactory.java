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
package io.bsoa.rpc.codec;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bsoa.rpc.ext.ExtensionLoader;
import io.bsoa.rpc.ext.ExtensionLoaderFactory;

/**
 * <p>序列化工厂</p>
 * <p>
 * Created by zhangg on 2016/12/24 21:09. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public final class SerializerFactory {

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SerializerFactory.class);

    /**
     * 除了托管给扩展加载器的工厂模式（保留alias：实例）外<br>
     * 还需要额外保留编码和实例的映射：{编码：序列化器}
     */
    private final static ConcurrentHashMap<Byte, Serializer> TYPE_SERIALIZER_MAP = new ConcurrentHashMap<>();

    /**
     * 扩展加载器
     */
    private final static ExtensionLoader<Serializer> extensionLoader
            = ExtensionLoaderFactory.getExtensionLoader(Serializer.class, extensionClass -> {
        // 除了保留 alias：Serializer外， 需要保留 code：Serializer
        TYPE_SERIALIZER_MAP.put(extensionClass.getCode(), extensionClass.getExtInstance());
    });


    /**
     * 按序列化名称返回协议对象
     *
     * @param alias 序列化名称
     * @return 序列化器
     */
    public static Serializer getSerializer(String alias) {
        // 工厂模式  托管给ExtensionLoader
        return extensionLoader.getExtension(alias);
    }

    /**
     * 按序列化名称返回协议对象
     *
     * @param type 序列号编码
     * @return 序列化器
     */
    public static Serializer getSerializer(byte type) {
        return TYPE_SERIALIZER_MAP.get(type);
    }

}
