package org.rpc4j.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.rpc4j.common.RpcDecoder;
import org.rpc4j.common.RpcEncoder;
import org.rpc4j.common.RpcRequest;
import org.rpc4j.common.RpcResponse;
import org.rpc4j.common.registry.ServiceRegistry;
import org.rpc4j.server.annotation.RPCService;
import org.rpc4j.server.handler.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * RPC服务器-(用于发布 RPC服务)
 */

// 在rpc4j-sample-server中的spring.xml配置为bean，并使用构造注入传入serverAddress与serviceRegistry
public class RPCServer implements ApplicationContextAware, InitializingBean {

    private static final Logger Logger = LoggerFactory.getLogger(RPCServer.class);

    private String serverAddress;
    private ServiceRegistry serviceRegistry;

    // 存放接口名与服务对象之间的映射关系
    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    public RPCServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RPCServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        //获取所有带 @RPCService 注解的 Bean
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RPCService.class);

        // 存放接口名与Bean之间的映射关系
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RPCService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    // 此处，参考netty官方的 user guide：http://netty.io/wiki/user-guide-for-4.x.html
    @Override
    public void afterPropertiesSet() throws Exception {            //在bean创建并注入好后执行
        EventLoopGroup bossGroup = new NioEventLoopGroup();        // NioEventLoopGroup: 处理I/O操作的多线程事件循环器
        EventLoopGroup workerGroup = new NioEventLoopGroup();      // boss：接收进来的链接，worker：处理已经接收的链接
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();     // ServerBootstrap：启动NIO服务的辅助启动类
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))      // 解码 RPC 请求
                                    .addLast(new RpcEncoder(RpcResponse.class))     // 编码 RPC 响应
                                    .addLast(new RpcHandler(handlerMap));           // 处理 RPC 请求
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");            // serverAddress = "127.0.0.1:8000"
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            Logger.debug("server started on port {}", port);

            if (serviceRegistry != null) {
                serviceRegistry.register(serverAddress);          // 注册 服务地址
            }

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
