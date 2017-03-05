/*
 * Copyright 2016 The BSOA Project
 *
 * The BSOA Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.bsoa.rpc.protocol.bsoa;

import io.bsoa.rpc.common.json.JSON;
import io.bsoa.rpc.exception.BsoaRuntimeException;
import io.bsoa.rpc.message.NegotiationRequest;
import io.bsoa.rpc.protocol.ProtocolNegotiator;
import io.bsoa.rpc.transport.ChannelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <p></p>
 * <p>
 * Created by zhangg on 2017/03/2017/3/4 12:43. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public class ServerRebootNegotiationHandler implements ProtocolNegotiator.NegotiationHandler {

    /**
     * slf4j Logger for this class
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerRebootNegotiationHandler.class);

    @Override
    public String command() {
        return "serverReboot";
    }

    @Override
    public String handle(NegotiationRequest request, ChannelContext context) throws BsoaRuntimeException {
        String data = request.getData();
        Map<String, String> map = JSON.parseObject(data, Map.class);
        LOGGER.info("server will reboot, info:{}", map);
        return "true";
    }
}
