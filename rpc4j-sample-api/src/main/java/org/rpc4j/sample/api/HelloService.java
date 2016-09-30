package org.rpc4j.sample.api;

/**
 * 对外发布的服务接口
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);
}
