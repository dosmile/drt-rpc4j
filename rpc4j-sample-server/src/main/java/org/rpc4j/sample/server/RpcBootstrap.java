package org.rpc4j.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RpcBootstrap {

    //启动service，并对外发布
    //由于在spring.xml中配置了zookeeper服务器、RPC服务器，因而当spring启动之后，就能对外发布HelloServiceImpl这个服务了
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
