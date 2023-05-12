# 关于目录

```
yarn-related
├── hadoop-files
│   ├── etc
│   │   └── hadoop                              配置文件目录
│   │       ├── capacity-scheduler.xml          配置容量调度器
│   │       ├── core-site.xml                   配置Namenode及对应端口
│   │       ├── fair-scheduler.xml              配置公平调度器
│   │       ├── hadoop-env.sh                   加入环境变量
│   │       ├── hadoop-policy.xml
│   │       ├── hdfs-site.xml                   配置文件系统
│   │       ├── kms-site.xml
│   │       ├── mapred-site.xml                 配置调度器选择及log Server端口
│   │       ├── workers                         指明工作节点
│   │       ├── yarnservice-log4j.properties    log选项
│   │       └── yarn-site.xml                   配置节点资源
│   └── sbin                                    Hadoop 脚本
│       ├── start-all.sh                        一键启动集群
│       ├── start-dfs.sh                        启动HDFS文件系统
│       ├── start-yarn.sh                       启动Yarn
│       ├── stop-all.sh                         一键关闭集群
│       ├── stop-dfs.sh                         关闭HDFS文件系统
│       ├── stop-yarn.sh                        关闭Yarn
│       ├── workers.sh                          扫描工作节点
│       ├── yarn-daemon.sh                      Yarn启动后的命令中转
│       └── yarn-daemons.sh
├── hosts
├── Mapreduce-demo-csvcount                     MapReduce测试任务与Yarn-Client测试任务
│   ├── csvcount.jar
│   ├── pom.xml
│   ├── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── csob
│       │   │           ├── mapreduce
│       │   │           │   └── csvcount
│       │   │           │       ├── CsvCountDriver.java
│       │   │           │       ├── CsvCountMapper.java
│       │   │           │       └── CsvcountReducer.java
│       │   │           └── yarn
│       │   │               └── client
│       │   │                   ├── ApplicationMaster.java
│       │   │                   └── Client.java
│       │   └── resources
│       │       └── log4j.properties
│       └── test
│           └── java
│   
├── profile                                     更新环境变量
├── README.md
└── ssh-files
    ├── authorized_keys                         内网节点互信
    ├── id_rsa
    └── id_rsa.pub

36 directories, 47 files
```

# 部署步骤
## 内网ssh免登录
- 了解本地ip，并在`/etc/hosts`中将ip与机器代号做对应
- 在`/root`中生成ssh并把对应机器的pub_keys加入到authorized_keys文件中
## 配置Hadoop环境
- 分别下载`Jdk 1.8.0` 和 `Hadoop 3.1.3`的.tar.gz包并解压重构到指定目录（`Hadoop 3.1.3`及以上版本对`yarn`的配置有重构，不再适用于本配置流程）
- 在`/etc/profile`中加入`JAVA_HOME`以及`HADOOP_HOME`及对应`bin/`、`sbin/`目录

## 指定Yarn配置
本次Yarn配置如下:

|master|node1|node2|
|----|----|----|
|NameNode,DataNode|DataNode|DataNode|
|NodeManager|ResourceManager,NodeManager|NodeManager|
|HistoryServer|-|-|

并在`$HADOOP_HOME/etc/hadoop`中的对应文件中进行配置，具体在文件中已有注释

配置完成后，在`$HADOOP_HOME/sbin`中加入Yarn相关配置或直接复制本目录中的文件（此配置与环境变量无关）

# 启动Hadoop Yarn 集群
依次

启动HDFS文件系统 `$HADOOP_HOME/sbin/start-dfs.sh` in NameNode节点

启动Yarn调度器 `#HADOOP_HOME/sbin/start-yarn.sh` in ResourceManager节点

HDFS加入文件 `hadoop -fs -mkdir [dir-name]` `haoop -fs -put [from-file-path] [to-file-path]`

运行样例任务 `hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.3.jar wordcount [input-path] [output-path]`

# 集群状态观察

HDFS文件系统网页 : 见`hdfs-site.xml`设置，本项目为 `master:9870`

Yarn 集群、任务状态网页 : 见 `yarn-site.xml`设置，本项目默认为 `node1:8088`

# 关于Mapreduce-demo-csvcount
此为测试`HDFS+MApReduce+Yarn`的`Hadoop`完整工作流程的一个计算任务，在打包成`.jar`包后可以进入`Hadoop`计算流程

关于`mapreduce`部分，具体指令为`hadoop jar csvcount.jar com.csob.mapreduce.csvcount.CsvCountDriver /input /output`

关于`yarn-client`部分，此为测试脱离MapReduce组件的Yarn流程的可执行文件调用，后经测试性能远不如`Java`原生，遂放弃
