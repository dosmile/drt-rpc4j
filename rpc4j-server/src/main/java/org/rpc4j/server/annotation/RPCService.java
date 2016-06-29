package org.rpc4j.server.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 请求注解（标注在服务实现类上）
 *
 * java中元注解有四个： @Retention @Target @Document @Inherited
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component                             //能够让使用了@RPCService 注解的 对象被 spring扫描
public @interface RPCService {

	Class<?> value();
	
}
