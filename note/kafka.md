## kafka基础配置
### broker配置
+ **broker.id**
  ```
  每个broker的标识符，在kafka集群中必须唯一
  ```
+ **zookeeper.connect**
  ```
  用于保存broker元数据的zookeeper地址，host:port格式
  ```
+ **log.dirs**
  ```
  保存kafka日志片段的磁盘目录，可配置多个，broker会往拥有最少数目分区的路径增加新分区
  ```
+ **num.recovery.threads.per.data.dir**
  ```
  每个日志文件夹的线程数，对如下3中情况kafka会使用可配置的线程池来处理日志片段
   - 服务器正常启动，用于打开每个分区的日志片段
   - 服务器崩溃重启后，用于检查和截短每个分区的日志片段
   - 服务器正常关闭，用于关闭日志片段
  ```
+ **auto.create.topics.enable**
  ```
  默认情况下，kafka会在如下几种情况下自动创建topic
   - 当一个生产者开始往topic写入消息时
   - 当一个消费者开始从topic读取消息时
   - 当任意一个客户端向topic发送元数据请求时
  ```
+ **num.partitions**
  ```
  指定了新创建的topic包含多少个分区，如果auto.create.topics.enable设置为true（启用自动创建topic），分区数就是指定的值，
  topic的分区数以后可以增加，但是不能减少（可能丢失数据），如果需要高吞吐量、高并发需要将分区数设置为较大的值
  ```
+ **replication.factor**
+ **default.replication.factor**
  ```
  主题级别的配置参数为replication.factor，broker级别的配置参数为default.replication.factor
  如果复制系数为N，那么在N-1个broker失效的情况下，仍然能够读取和写入数据，更高的复制系数带来更高的可用性、可靠性和更少的
  故障，但是复制系数N需要至少N个broker，而且会有N个副本，也就是说会占用N倍的磁盘空间
  ```
+ **log.retention.hours**
+ **log.retention.minutes**
+ **log.retention.ms**
  ```
  设置数据可以被保留多长时间，默认使用hours配置，默认值为168小时即一周，如果指定了多个参数，则按照值最小的为准
  ```
+ **offsets.retention.minutes**
  ```
  指定消费者组已提交偏移量的保留时间，默认为1440（即一天），当超过该时间后该偏移量记录就会删除，如果消费者重启则消息会被重新消费
  可以将该值与log.retention.hours设置相同
  ```
+ **log.retention.bytes**
  ```
  设置每个分区保留的最大消息字节数，与log.retention.hours共同使用时，只要任意一个条件得到满足消息就会删除
  ```
+ **log.segments.bytes**
  ```
  设置日志片段的大小，默认为1GB，当消息到达broker时，它们会被追加到某个分区的当前日志片段上，当日志片段大小达到设定值时，
  当前日志片段就会被关闭，新的日志片段将开启。
  * 注：日志片段在关闭前其中的消息不会过期，
  ```
+ **log.segments.ms**
  ```
  日志片段关闭时间，指定多长时间后当前日志片段会被关闭，与log.segments.bytes共用时，任意一个条件满足即关闭日志片段
  ```
+ **message.max.bytes**
  ```
  限制单个消息的大小（压缩后），默认值为1000000（1M）
  ```
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
### 生产者配置
+ **bootstrap.servers**
  ```
  指定broker的地址清单，格式为host:port，不需要全部指定，生产者会从给定的broker里查找到其它的broker信息，不过一般建议
  至少指定两个，即使其中一个宕机后生产者仍然能够连接到broker
  ```
 + **key.serializer**
 + **value.serializer**
  ```
  broker希望接收到的消息的key-value都是字节数组。生产者接口允许使用参数化类型，因此需要指定合适的序列化机制将key-value转换
  为字节数组。kafka客户端默认提供了了ByteArraySerializer、StringSerializer、IntergerSerializer，如果需要其它自定义类型
  对象作为key-value，则需要实现org.apache.kafka.common.serialization.Serializer接口自定义序列化器，该配置必须指定
  ```
+ **acks**
  ```
  指定必须有多少个分区副本收到消息，生产者才会认为消息写入成功
   - 如果acks=0，则生产者在成功写入消息之前不会等待服务器响应。发送速度快，吞吐量高，但消息会丢失
   - 如果acks=1，只要集群的首领节点接收到消息，生产者就会收到一个来自服务器的成功响应。
   - 如果acks=all，只有当所有的副本节点都收到消息时，生产者才会收到来自服务器的成功响应。此模式最安全但延迟更高
  ```
+ **buffer.memory**
  ```
  生产者缓冲区大小，用来缓冲要发送到服务器的消息
  ```
+ **compression.type**
  ```
  消息压缩方式，默认不压缩。可设置none、snappy、gzip、lz4
  ```
+ **retries**
  ```
  生产者收到服务器的临时错误（如分区无首领）时，生产者重复发送消息的次数。默认的时间间隔为100ms，可以通过retry.backoff.ms指定时间间隔
  ```
+ **batch.size**
  ```
  当有多条消息需要发送到同一个分区时，生产者会把它们放到一个批次里。该参数指定批次可以使用的内存大小（按字节算）
  ```
+ **linger.ms**
  ```
  该参数指定了生产者发送批次之前等待更多消息加入批次的时间。KafkaProducer会在批次填满或者linger.ms达到上限时将批次发送出去
  ```
+ **max.in.flight.requests.per.connection**
  ```
  该参数指定生产者在收到服务器响应之前可以发送多少条消息。值越高内存占用越大，相应的也会提升吞吐量，设为1可以保证消息按照发送的顺序写入
  服务器，即使发生了重试
  ```
+ **timeout.ms**
+ **request.timeout.ms**
+ **metadata.fetch.timeout.ms**
  ```
  request.timeout.ms指定了生产者在发送数据时等待服务器响应的时间，metadata.fetch.timeout.ms指定了生产者在获取元数据（比如目标分区
  的首领是谁）时等待服务器返回响应的时间，如果等待响应超时，那么生产者要么重试发送数据，要么返回一个错误。timeout.ms指定了broker等待
  同步副本返回消息确认的时间，与asks的配置相匹配--如果在指定的时间内没有收到同步副本的确认，那么broker就会返回一个错误。
  ```
+ **max.block.ms**
  ```
  该参数指定了在调用send()方法或使用partitionsFor()方法获取元数据时生产者的阻塞时间。当生产者的发送缓冲区已满，或者没有可用的元数据
  时，这些方法就会阻塞，在阻塞时间达到max.block.ms时，生产者就会抛出超时异常。
  ```
+ **max.request.size**
  ```
  该参数用于控制生产者发送的请求大小，如果单个消息发送则指单条消息大小，如果批次发送则指该批次消息总大小。broker对可接收的消息的最大值
  也做了限制（message.max.bytes），两边的设置最好匹配，避免生产者发送的消息被broker拒收
  ```
+ **receive.buffer.size**
+ **send.buffer.size**
  ```
  指定了TCP socket接收和发送数据包的缓冲区大小，如果设为-1则使用操作系统的默认值。如果broker处于不同的数据中心，可以适当增加这些值，
  因为跨数据网络一般有比较高的延迟和比较低的带宽
  ```
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
### 消费者配置
+ **bootstrap.servers**
  ```
  指定broker的地址清单，格式为host:port，不需要全部指定，生产者会从给定的broker里查找到其它的broker信息，不过一般建议
  至少指定两个，即使其中一个宕机后生产者仍然能够连接到broker
  ```
 + **key.serializer**
 + **value.serializer**
  ```
  序列化器。kafka客户端默认提供了了ByteArraySerializer、StringSerializer、IntergerSerializer，如果需要其它自定义类型
  对象作为key-value，则需要实现org.apache.kafka.common.serialization.Serializer接口自定义序列化器，该配置必须指定
  ```
+ **group.id**
  ```
  指定该消费者属于哪个消费者组。在同一个消费者组中的消费者共同协作完成同一topic的消息消费
  ```
+ **fetch.min.bytes**
  ```
  指定消费者从服务器获取记录的最小字节数。
  ```
+ **fetch.max.wait.ms**
  ```
  指定broker的等待时间，默认500ms，如果没有足够的数据流入kafka，消费者获取最小数据量就得不到满足，最终导致延迟fetch.max.wait.ms
  后返回
  ```
+ **max.partition.fetch.bytes**
  ```
  指定了服务器从每个分区里返回给消费者的最大字节数，默认值是1M。
  ```
+ **session.timeout.ms**
  ```
  指定消费者在被认为死亡之前可以与服务器断开连接的时间，默认3s
  ```
+ **heartbeat.interval.ms**
  ```
  指定了poll()方法向协调器发送心跳的频率，session.timeout.ms指定可以多久不发送心跳，一般该值设定为session.timeout.ms的三分之一
  ```
+ **auto.offset.reset**
  ```
  指定了消费者在读取一个没有偏移量的分区或者偏移量无效的情况下（因消费者长时间失效，包含偏移量的记录以及过时并被删除）该如何处理，默认值
  是latest，即消费者从最新的记录开始读取数据（在消费者启动之后生成的记录），另一个是earliest，从起始位置读取数据
  ```
+ **enable.auto.commit**
  ```
  指定是否自动提交偏移量，默认是true，auto.commit.interval.ms指定自动提交偏移量频率
  ```
+ **max.poll.records**
  ```
  用于控制单次调用call()方法能够返回的记录数
  ```
