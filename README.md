# imooc-dubbo
zookeeper dubbo 分布式锁实现

spring + curator 操作zookeepr（本地单机模式 127.0.0.1）
消费者：购买商品
服务提供者：订单服务 order，商品服务 item

分布式锁使用 zookeeper PathChildrenCache 异步监听指定节点创建删除实现

PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
创建订单开始时，zookeeper目录下创建一个临时节点，创建成功表示获得分布式锁，否则一直等待锁的释放
