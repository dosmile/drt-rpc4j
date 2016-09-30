package org.rpc4j.common.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务注册
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);                                  //同步计数器

    private String registryAddress;               // zookeeper 服务器的地址

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    //唯一的一处调用：data = serverAddress ？？
    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();      //ZooKeeper Client，【在client链接到server之前，会一直阻塞于此】
            if (zk != null) {
                createNode(zk, data);            //创建节点
            }
        }
    }

    // zookeeper client 要链接到 zookeeper server 才能够使用
    private ZooKeeper connectServer() {
		ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {     // zookeeper client connected to server
                        latch.countDown();
                    }
                }
            });
            latch.await();                                       //在zookeeper client 链接到 server之前，保持阻塞
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            Stat stat = zk.exists(Constant.ZK_REGISTRY_PATH, false);

            //若不存在”/registry“节点，就先创建这个节点
            if(stat==null) {
            	String path1 = zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            //在”/registry“节点下，继续创建“/data”节点
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}