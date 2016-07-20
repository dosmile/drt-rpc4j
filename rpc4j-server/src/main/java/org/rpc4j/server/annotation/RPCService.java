package org.rpc4j.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 请求注解（标注在api的实现类上）
 *
 * java中元注解有四个： @Retention @Target @Document @Inherited
 * 元注解：修饰注解的注解
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)    //表示注解的信息保留在class文件中，在虚拟机运行时也会保留,是的在运行时能够通过反射拿到注解信息？
@Component                             //能够让使用了@RPCService 注解的 对象被 spring扫描
public @interface RPCService {

	Class<?> value();
	
}
