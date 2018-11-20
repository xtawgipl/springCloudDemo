# spring cloud 例子、原理、自实现
## 配置中心
* 基于git配置
* 基于自实现配置

## 服务注册
* 基于zookeeper配置

## 服务发现

## 负载均衡
* 基于Ribbon实现
* 基于DiscoverService自实现
    * DiscoverService 服务拉取————写时拷贝实现
    * RestTemplate 插件实现随机负载
    * CustomizedLoadBalanced 注解实现

## 熔断
* 基于Hystrix实现
* 自实现
    * 注解 + Future + timeout 实现
    * 注解 + Semaphore 实现
    * 注解 + limiter 实现

