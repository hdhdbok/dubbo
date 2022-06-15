### Dubbo模块的作用
| 模块名称 | 作用                                                         |
| :-------------- | :----------------------------------------------------------- |
| dubbo-common    | 通用逻辑模块，提供工具类和通用模型                           |
| dubbo-remoting  | 远程通信模块，为消费者和服务提供者提供通信能力               |
| dubbo-rpc       | 容易和remote模块混淆，本模块抽象各种通信协议，以及动态代理   |
| dubbo-cluster   | 集群容错模块，RPC只关心单个调用，本模块则包括负载均衡、集群容错、路由、分组聚合等 |
| dubbo-registry  | 注册中心模块                                                 |
| dubbo-monitor   | 监控模块，监控Dubbo接口的调用次数、时间等                    |
| dubbo-config    | 配置模块，实现了 API配置、属性配置、XML配置、注解配置等功能  |
| dubbo-container | 容器模块，如果项目比较轻量，没用到Web特性，因此不想使用Tomcat等Web容器，则可以使用这个Main方法加载Spring的容器 |
| dubbo-filter    | 过滤器模块，包含Dubbo内置的过滤器                            |
| dubbo-plugin    | 插件模块，提供内置的插件，如QoS                              |
| dubbo-demo      | 一个简单的远程调用示例模块                                   |
| dubbo-test      | 测试模块，包含性能测试、兼容性测试等                         |
