# rpc4j

轻量级 RPC framework

参考来源：
http://my.oschina.net/huangyong/blog/361751
http://www.oschina.net/code/snippet_223750_45050

## 项目所选用的相关技术：
Spring：它是最强大的依赖注入框架，也是业界的权威标准。
Netty：它使 NIO 编程更加容易，屏蔽了 Java 底层的 NIO 细节。
Protostuff：它基于 Protobuf 序列化框架，面向 POJO，无需编写 .proto 文件。
ZooKeeper：提供服务注册与发现功能，开发分布式系统的必备选择，同时它也具备天生的集群能力。

## 运行
1. 首先启动zookeeper
2. 运行RpcBootstrap
3. 通过Junit运行 rpc-sample-client中的test