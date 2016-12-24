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
package io.bsoa.rpc.transport.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bsoa.rpc.common.SystemInfo;
import io.bsoa.rpc.exception.BsoaRuntimeException;
import io.bsoa.rpc.ext.Extension;
import io.bsoa.rpc.transport.ServerTransport;
import io.bsoa.rpc.transport.ServerTransportConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p></p>
 * <p>
 * Created by zhangg on 2016/12/17 22:06. <br/>
 *
 * @author <a href=mailto:zhanggeng@howtimeflies.org>GengZhang</a>
 */
@Extension("netty4")
public class NettyServerTransport implements ServerTransport {

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyServerTransport.class);

    private ServerTransportConfig transportConfig;
    private ServerBootstrap serverBootstrap;

    @Override
    public boolean start() {
        boolean flag = Boolean.FALSE;
        LOGGER.debug("BSOA server transport start! ");
        if (SystemInfo.isWindows()) {
            transportConfig.setReuseAddr(false);  //disable this on windows,
        }
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(
                NettyTransportHelper.getServerBossEventLoopGroup(transportConfig),
                NettyTransportHelper.getServerIoEventLoopGroup(transportConfig))
                .channel(transportConfig.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, transportConfig.getBacklog())
                .option(ChannelOption.SO_REUSEADDR, transportConfig.isReuseAddr())   //disable this on windows, open it on linux
                .childOption(ChannelOption.SO_KEEPALIVE, transportConfig.isKeepAlive())
                .childOption(ChannelOption.TCP_NODELAY, transportConfig.isTcpNoDelay())
                .childOption(ChannelOption.ALLOCATOR, NettyTransportHelper.getByteBufAllocator())
                .childOption(ChannelOption.SO_RCVBUF, 8192 * 128)
                .childOption(ChannelOption.SO_SNDBUF, 8192 * 128)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, transportConfig.getc()) // ????????
//                 .childOption(ChannelOption.SO_TIMEOUT, config.getTIMEOUT())
                .childHandler(new NettyServerChannelInitializer(transportConfig));

        // 绑定到全部网卡 或者 指定网卡
        ChannelFuture future = serverBootstrap.bind(
                new InetSocketAddress(transportConfig.getHost(), transportConfig.getPort()));
        ChannelFuture channelFuture = future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Server have success bind to {}:{}",
                                transportConfig.getHost(), transportConfig.getPort());
                    }
                } else {
                    LOGGER.error("Server fail bind to {}:{}",
                            transportConfig.getHost(), transportConfig.getPort());
                    NettyTransportHelper.closeServerBossEventLoopGroup(transportConfig);
                    NettyTransportHelper.closeServerIoEventLoopGroup(transportConfig);
                    throw new BsoaRuntimeException(22222, "Server start fail !", future.cause());
                }
            }
        });

        try {
            channelFuture.await(5000, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                flag = Boolean.TRUE;
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return flag;
    }

    @Override
    public void stop() {
        LOGGER.info("Shutdown the BSOA server transport now...");
        NettyTransportHelper.closeServerBossEventLoopGroup(transportConfig);
        NettyTransportHelper.closeServerIoEventLoopGroup(transportConfig);
    }

    @Override
    public void setConfig(ServerTransportConfig transportConfig) {
        this.transportConfig = transportConfig;
    }
}
