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
package io.bsoa.rpc.message;

import io.bsoa.rpc.transport.AbstractByteBuf;

/**
 * <p></p>
 *
 * Created by zhangg on 2016/12/20 21:45. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
public class StreamMessage extends BaseMessage {

    protected short frameId;

    private boolean EndOfStream;

    private AbstractByteBuf byteBuf;

    public short getFrameId() {
        return frameId;
    }

    public void setFrameId(short frameId) {
        this.frameId = frameId;
    }

    public boolean isEndOfStream() {
        return EndOfStream;
    }

    public void setEndOfStream(boolean endOfStream) {
        EndOfStream = endOfStream;
    }

    public AbstractByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(AbstractByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }
}
