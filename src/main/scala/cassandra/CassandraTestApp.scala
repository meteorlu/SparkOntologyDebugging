package cassandra

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by user on 2017/5/24.
  */
object CassandraTestApp {
  def main(args: Array[String]) {
    //配置spark，cassandra的ip，这里都是本机
    val SparkMasterHost = "127.0.0.1"

    // Tell Spark the address of one Cassandra node:
    val conf = new SparkConf(true)
      .setMaster("local[12]")
      .setAppName("CassandraTestApp")

    // Connect to the Spark cluster:
    lazy val sc = new SparkContext(conf)
    CassandraDB.connect()
    var resultrows = CassandraDB.session.execute("select * from mrjks.resources;");
    try{
      import scala.collection.JavaConversions._
      for (row <- resultrows) {
        System.out.println( row.getLong("id")+"\t"+row.getString("label"))
      }
    }finally {
      CassandraDB.close()
    }
  }
}
