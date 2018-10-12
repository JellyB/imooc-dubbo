# imooc-dubbo
zookeeper + dubbo 实现分布式锁

`spring + curator` 操作 `zookeepr`（本地单机模式 127.0.0.1:2181）
消费者：购买商品
服务提供者：订单服务 order，商品服务 item

消费者服务在模块`dubbo-web`中，引入了接口`item-api`和`odder-api`
生产者服务由两部分提供`item-service` & `order-service`

分布式锁使用 `zookeeper` `PathChildrenCache` 异步监听指定父节点下子节点的创建删除实现

```java
PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
```

#### 分布式锁初始化命名空间：
```java
private void init (){
         client.usingNamespace(nameSpace);
         /**
          *    创建zk 总节点
          *    namespace : ZK-locks-nameSpace
          *                    |
          *                     --  imooc-locks
          *                            |
          *                             -- distributed-lock
          */
         try{
             if(client.checkExists().forPath( SEPARATOR + ZK_LOCK_PROJECT) == null){
                 client.create()
                         .creatingParentsIfNeeded()
                         .withMode(CreateMode.PERSISTENT)
                         .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                         .forPath(SEPARATOR + ZK_LOCK_PROJECT);
             }
             addWatcherToLock(SEPARATOR + ZK_LOCK_PROJECT);
         }catch (Exception e){
             log.error("分布式锁初始化失败！");
         }
     }
```
   
#### 请求到达获得分布式锁实现：

> 获取锁，如果成功获取锁，countDownLath -1， 否则一直等待
> 锁的实现 -- 创建子节点 DISTRIBUTED_LOCK

```java
     public void getLock(){
 
         /**
          *   使用死循环，当且仅当上一个锁释放并且当前请求获得锁之后才可以跳出
          */
         while (true){
 
             try{
                 /**
                  * 如果节点可以创建，说明锁没有被占用
                  */
                 client.create()
                         .creatingParentsIfNeeded()
                         .withMode(CreateMode.EPHEMERAL)
                         .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                         .forPath(SEPARATOR + ZK_LOCK_PROJECT + SEPARATOR + DISTRIBUTED_LOCK);
 
                 log.info("获得分布式锁成功！");
                 return;
             }catch (Exception e){
                 log.error("获取分布式锁锁失败");
                 try{
                     /**
                      * 如果没有获得分布式锁，则需要重新设置同步资源值
                      */
                     if(getCountDownLatch().getCount() <= 0){
                         log.info("*********************");
                         countDownLatch = new CountDownLatch(1);
                     }
                     countDownLatch.await();
                 }catch (Exception e1){
                     log.error("设置countDown失败！", e);
                 }
             }
 
         }
     }
```

#### zk节点监听节点变化：
> 分布式锁子节点添加watch事件，监听父节点子节点的移除--锁的释放

```
 public void addWatcherToLock(String path) throws Exception{
     final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
     pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

     pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
         @Override
         public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
             if(event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED){
                 String path = event.getData().getPath();
                 log.info("上一个会话已结束或已经释放锁路径：{}" ,path);
                 if(path.contains(ZK_LOCK_PROJECT)){
                     log.info("释放计数器，当前线程可获得分布式锁");
                     countDownLatch.countDown();
                 }
             }
         }
     });
 }
 ```
 
          
 #### 当前线程请求处理完释放锁：
 
 > 释放锁操作
 > return 成功过 true 失败 false
 ```
      public boolean releaseLock(){

          try{
              if(client.checkExists().forPath(SEPARATOR + ZK_LOCK_PROJECT + SEPARATOR + DISTRIBUTED_LOCK) != null){
                  client.delete().forPath(SEPARATOR + ZK_LOCK_PROJECT + SEPARATOR + DISTRIBUTED_LOCK);
              }
          }catch (Exception e){
              log.error("释放锁失败！", e);
              return false;
          }
          log.info("分布式锁释放成功！");
          return true;
      }
 ```
 
  ***
  
> 关键点：
    获取锁，CreateMode 指定为临时节点，如果上一个会话被关闭后不至于一直持有锁
    countDownLatch 阻塞线程
  
2.6.0 dubbo monitor 现在地址：
    https://github.com/alibaba/dubbo/archive/dubbo-2.6.0.zip

