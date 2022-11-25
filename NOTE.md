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

### @Import
> 当 Spring 容器启动的时候，如果注解上面使用 @Import, 则会触发其注解方法 selectimports,
比如 EnableDubboConfig 注解中指定的 DubboConfigConfigurationSelector.class, 会自动触
发 DubboConfigConfigurationSelector#selectImports 方法
> 如果业务方配置了 Spring 的 @PropertySource 或 XML 等价的配置(比如配置了框架 dubbo.registry.address 和
dubbo.application 等属性)，则 Dubbo 框架会在 DubboConfigConfigurationSelectorttselectlmports 中自动生成相应的配置承载对象，比如 Applicationconfig 等。
> 细心的读者可能发现 DubboConfigConfiguration 里面标注了@EnableDubboConfigBindings, @EnableDubboConfigBindings
同样指定了@Import(DubboConfigBindingsRegistrar.class)。因为@EnableDubboConfigBindings
允许指定多个@EnableDubboConfigBinding注解,Dubbo会根据用户配置属性自动填充这些承载
的对象

### 配置承载初始化
> 不管在服务暴露还是服务消费场景下，Dubbo框架都会根据优先级对配置信息做聚合处理,
目前默认覆盖策略主要遵循以下几点规则：
> - (1) -D 传递给 JVM 参数优先级最高，比如-Ddubbo. protocol.port=20880o
> - (2) 代码或XML配置优先级次高，比如Spring中XML文件指定<dubbo:protocol
port=H20880'7>o
> - (3)配置文件优先级最低，比如 dubbo.properties 文件指定 dubbo.protocol.port=20880o
一般推荐使用dubbo.properties作为默认值，只有XML没有配置时，dubbo.properties
配置项才会生效，通常用于共享公共配置，比如应用名等。

> Dubbo的配置也会受到provider的影响，这个属于运行期属性值影响，同样遵循以下几点
规则：
> - (1) 如果只有provider端指定配置，则会自动透传到客户端(比如timeout)o
> - (2) 如果客户端也配置了相应属性，则服务端配置会被覆盖(比如timeout)o
运行时属性随着框架特性可以动态添加，因此覆盖策略中包含的属性没办法全部列出来，
一般不允许透传的属性都会在ClusterUtils#mergellrl中进行特殊处理。