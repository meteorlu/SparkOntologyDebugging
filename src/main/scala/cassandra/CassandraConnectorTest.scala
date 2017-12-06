package cassandra

import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._



/**
  * Created by user on 2017/11/15.
  */
object CassandraConnectorTest {
  def main(args:Array[String]){
    // 配置spark，cassandra的ip，这里都是本机
    val SparkMasterHost = "local"
    val CassandraHost = "127.0.0.1"
    // Tell Spark the address of one Cassandra node:
    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", CassandraHost)
      .set("spark.cleaner.ttl", "3600")
      .setMaster(SparkMasterHost)
      .setAppName("CassandraTestApp")

    // Connect to the Spark cluster:
    lazy val sc = new SparkContext(conf)
    //预处理脚本,连接的时候就执行这些
    CassandraConnector(conf)
    //      .withSessionDo { session =>
    //      session.execute("CREATE KEYSPACE IF NOT EXISTS test WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
    //      session.execute("CREATE TABLE IF NOT EXISTS test.key_value (key INT PRIMARY KEY, value VARCHAR)")
    //      session.execute("TRUNCATE test.key_value")
    //      session.execute("INSERT INTO test.key_value(key, value) VALUES (1, 'first row')")
    //      session.execute("INSERT INTO test.key_value(key, value) VALUES (2, 'second row')")
    //      session.execute("INSERT INTO test.key_value(key, value) VALUES (3, 'third row')")
    //    }
//    println("connect finish!")
//    //      // Read table test.kv and print its contents:
////    val collection = sc.parallelize(Seq((4, "forth row"), (5, "fifth row")))
////    collection.saveToCassandra("test", "kv", SomeColumns("key", "value"))
//    println("excute end")
//    val conf = new SparkConf().setAppName("SimpleGraphX").setMaster("local")
//    val sc = new SparkContext(conf)
//    var rdd = sc.parallelize(1 to 100)
    println("end")
    val table = sc.cassandraTable("test", "key_value")
    println(table.count)
//          .select("key", "value")
//        rdd.collect().foreach(row => println(s"Existing Data: $row"))
    //
    //     Write two new rows to the test.kv table:
    //    val col = sc.parallelize(Seq((4, "fourth row"), (5, "fifth row")))
    //    col.saveToCassandra("test", "key_value", SomeColumns("key", "value"))
    //
    //     Assert the two new rows were stored in test.kv table:
    //    assert(col.collect().length == 2)
    //
    //    col.collect().foreach(row => println(s"New Data: $row"))
    //    println(s"Work completed, stopping the Spark context.")
    //    sc.stop()
  }
}
