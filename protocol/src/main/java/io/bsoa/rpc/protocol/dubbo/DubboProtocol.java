/*
 * *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package io.bsoa.rpc.protocol.dubbo;

import io.bsoa.rpc.ext.Extension;
import io.bsoa.rpc.ext.ExtensionLoaderFactory;
import io.bsoa.rpc.protocol.Protocol;
import io.bsoa.rpc.protocol.ProtocolDecoder;
import io.bsoa.rpc.protocol.ProtocolEncoder;
import io.bsoa.rpc.protocol.ProtocolInfo;

/**
 * <p></p>
 * <p>
 * Created by zhangg on 2016/12/18 09:22. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
@Extension("dubbo")
public class DubboProtocol implements Protocol {

    private ProtocolInfo protocolInfo = new DubboProtocolInfo();

    public DubboProtocol() {
        // 注册协议自适应
    }

    @Override
    public ProtocolInfo protocolInfo() {
        return protocolInfo;
    }

    @Override
    public ProtocolEncoder encoder() {
        ProtocolEncoder encoder = ExtensionLoaderFactory.getExtensionLoader(ProtocolEncoder.class)
                .getExtension("dubbo");
        encoder.setProtocolInfo(protocolInfo);
        return encoder;
    }

    @Override
    public ProtocolDecoder decoder() {
        ProtocolDecoder decoder = ExtensionLoaderFactory.getExtensionLoader(ProtocolDecoder.class)
                .getExtension("dubbo");
        decoder.setProtocolInfo(protocolInfo);
        return decoder;
    }

    @Override
    public byte getCode() {
        return 3;
    }
}
