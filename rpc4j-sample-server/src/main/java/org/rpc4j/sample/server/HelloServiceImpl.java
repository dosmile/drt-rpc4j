package org.rpc4j.sample.server;

import org.rpc4j.sample.api.HelloService;
import org.rpc4j.sample.api.Person;
import org.rpc4j.server.annotation.RPCService;

/**
 * 发布的服务的具体实现
 * 使用RPCService注解来声明这是一个RPC服务，同时以使RPC server能够知晓服务对应的接口
 * 因为一个impl可能实现了很多的接口，必须得让 rpc server知道哪一个才是其对外的服务接口
 */
@RPCService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
