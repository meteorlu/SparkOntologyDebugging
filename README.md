# 代码相关
本工程实现的是Spark下的本体调试算法实现以及改进。
相关内容介绍如下：
scala文件夹下为工程主体代码
1、其中cassandra文件夹下的CassandraDB类为连接Cassandra数据库的工具类；
2、utils文件夹实现的是一些工程使用到的工具类；
3、graphx文件夹下为调试算法的实现和优化：
    a.CollectionTripleNews为获取三元组相关的依赖子图的深度、顶点数和边数；
    b.Justification_mapreduce为调试算法的实现，实现方式类似Hadoop中的MapReduce计算模型的实现；
    c.Justification_v系列文件为优化版本，最新的为7，通过构造依赖子图，GraphX求取一步辩解集合，迭代求取最终辩解；
 
#发布相关
 本工程建立的是maven依赖工程，所有相关配置已经在pom.xml中配置完毕，相关的版本号在其中也有相关的体现。
 本工程使用过的是IntelliJ IDEA开发工具，发的使用Build Artifacts，将发布完成的相关jar包拷贝到集群上面，使用spark-submit运行，具体运行有以下几点需要注意：
    1、如果在程序代码中配置了一些关于Spark的相关设置，那么运行程序的时候，优先级最高；
    2、如果想使用spark-submit动态配置，那么代码中就不需要进行任何配置；
    3、由于本工程依赖了其他的jar包，因此需要在spark的环境变量中指定相关jar的文件夹位置；


