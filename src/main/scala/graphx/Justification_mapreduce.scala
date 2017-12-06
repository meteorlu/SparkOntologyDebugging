package graphx

import java.io.{File, PrintWriter}

import cassandra.CassandraDB
import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.log4j.{Level, Logger}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.util.LongAccumulator
import utils.Triple

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer, Set}

/**
  * Created by user on 2017/5/25.
  */
object Justification_mapreduce {
  //vertex index
  var vindex: Long = 1L
  var eindex: Long = 1L
  var vertexMap: Map[(Long, Long, Long), Long] = Map()
  var searchMap: Map[Long, (Long, Long, Long)] = Map()
  var edgeMap: Map[(Long, Long, Long, Int), Long] = Map()
  var start: Long = 0L
  var end: Long = 0L
  var cost: Long = 0L
  var sub, pre, obj: Long = 0L
  var targetNum: Long = 0L
  var accum:LongAccumulator = null
  var resultJustification:Broadcast[Set[Set[Triple]]] = null
  var resultSize:Int = 0
  var theSameTime:Int = 0
  def main(args: Array[String]) {
    var i: Int = 0
    for (i <- 0 to args.length - 1) {
      if (args(i).equals("--subject"))
        sub = args(i + 1).toLong
      if (args(i).equals("--predicate"))
        pre = args(i + 1).toLong
      if (args(i).equals("--object"))
        obj = args(i + 1).toLong
    }
    if (sub == 0L && pre == 0L && obj == 0L) {
      println("please input args --subject --predicate --object")
      return
    }
    var triple = new Triple(sub, pre, obj, false)
    var explanation: Set[Triple] = Set(triple)
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

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
    //初始化累加器
    accum = sc.longAccumulator("Accumulator")
    resultJustification = sc.broadcast(Set())
    //预处理脚本,连接的时候就执行这些
    CassandraConnector(conf)
    //get data from cassandra
    start = System.nanoTime()
    try {
      CassandraDB.connect()
      //map 过程
      var rddInit: RDD[(Triple, Set[Triple])] = sc.makeRDD(Seq((triple, explanation)))
      findJustifications(rddInit)
    } catch {
      case ex: Exception => ex.printStackTrace()
    } finally {
      CassandraDB.close()
    }
    sc.stop()
  }

  //讲结果保存到文本文件
  def writeToResults(results: Set[ArrayBuffer[String]]) = {
    var writer = new PrintWriter(new File("D:\\results.txt"))
    results.foreach(r => writer.println(r))
    writer.close()
  }

  //求取辩解集合
  def findJustifications(rddInit: RDD[(Triple, Set[Triple])]): Unit = {
//    rddInit.foreach( x=>println("三元组："+x._1+"******* 辩解集和："+x._2))
    var mapRdd: RDD[(Set[Triple],Triple, Int)] = rddInit.flatMap(x => {
      var triple:Triple = x._1
      var explanation: Set[Triple] = x._2.clone()
      //输出的结果集合
      var results: Set[(Set[Triple],Triple, Int)] = Set()
      //创建一个不包括自身的集合
      val toExtendExplanations: Set[Triple] = x._2.clone()
      toExtendExplanations.remove(triple)
//      println("explanation:"+explanation)
//      println("toExtendExplanations:"+toExtendExplanations)
      //从数据库的justification表中查询推理前提条件
      //        var cassandRDD = sc.cassandraTable("mrjks","justifications")
      //          .select("sub","pre","obj","rule","tripletype","isliteral","v1","v2","v3")
      //          .where(" sub = ? and pre = ? and obj =?",x._1.subject,x._1.predicate,x._1._object)
      //          .aggregate()
      var tracingEntries: Set[Triple] = Set()
      var resultrows = CassandraDB.session.execute("select * from mrjks.justifications where sub=" + triple.subject + " and pre=" + triple.predicate + " and obj=" + triple._object + ";")
      import scala.collection.JavaConversions._
      for (row <- resultrows) {
        var tri = new Triple(
          row.getLong("sub"),
          row.getLong("pre"),
          row.getLong("obj"),
          row.getInt("rule"),
          row.getLong("v1"),
          row.getLong("v2"),
          row.getLong("v3"),
          row.getBool("isliteral"),
          null)
        tracingEntries.add(tri)
      }
      if (tracingEntries.isEmpty) {
//        println("【到头了】："+triple)
        results.add((explanation,triple,1))
      } else {
//        println("有前置条件tracingEntries："+tracingEntries)
//        println("前置条件如下：")
        tracingEntries.foreach(tracingEntry => {
          var tracedTriples: Set[Triple] = utils.Util.tracing(tracingEntry)
//          println(tracingEntry+"："+tracedTriples)
          if(!tracedTriples.contains(triple)){
            var newExplanation: Set[Triple] = toExtendExplanations.clone()
            tracedTriples.foreach(tracedTriple => {
              if(!newExplanation.exists(t => t.equals(tracedTriple))){
                newExplanation.add(tracedTriple)
              }
            })
            if(tracedTriples.isEmpty){
              results.add((explanation,triple,1))
            }else if(toExtendExplanations.size == newExplanation.size) {
              results.add((explanation,triple,1))
            }else{
              results.add((newExplanation,tracingEntry,0))
            }
          }
        })
      }
      results
    })
//    //打印map结果
//    println("reduceByKey之前：" + mapRdd.collect().size)
//    mapRdd.sortBy(x=>x._1.toString()).foreach(x => println("Value:"+x._3+"***** triple:"+x._2+" *****  "+"Key:"+x._1))
//    //进行reduce过程
//    var redRdd = mapRdd.map(x => (x._1,x._3)).reduceByKey(_+_)
//    println("reduceByKey之后：" + redRdd.collect().size)
//    redRdd.sortBy(x=>x._1.toString()).foreach(x => println("Value:"+x._2+" *****  "+"Key:"+x._1))
    var reduceRdd = mapRdd.map(x => (x._1,x._3)).reduceByKey(_+_).flatMap(x => {
      var results: Set[(Triple, Set[Triple])] = Set()
      if (x._2 == x._1.size) {
        //保存一个辩解结合
        if(!resultJustification.value.exists(result => compareSetTriples(result,x._1))){
          resultJustification.value.add(x._1)
        }
//        var writeTxt = new ArrayBuffer[String]()
//        resultJustification.foreach(just => {
//          writeTxt.addString(new StringBuilder(just.toString()))
//        })
      }else if(x._2 == 0) {
        x._1.foreach(triple => {
          results.add((triple, x._1))
        })
      }
      results
    })
    accum.add(1L)
//    println(accum.value)
    //判断辩解集和是否增加
    if(resultSize == resultJustification.value.size){
      theSameTime += 1
    }else if(resultJustification.value.size > resultSize){
      theSameTime = 0
      resultSize = resultJustification.value.size
    }
    //判断reduceRdd是否需要进一步迭代
    if (!reduceRdd.isEmpty() && accum.value < 10000L && theSameTime < 6) {
      println("rddInit:")
      println(rddInit.sortBy(x=>x._1.toString()).collect().foreach(println))
      println("reduceRdd:")
      println(reduceRdd.sortBy(x=>x._1.toString()).collect().foreach(println))
      findJustifications(reduceRdd)
    }else{
      resultJustification.value.foreach(j => println("#####just"+j))
    }
  }
  def compareSetTriples(set1:Set[Triple],set2:Set[Triple]): Boolean = {
    var result:Boolean = true
    if(set1.size != set2.size) {
      result = false
    }
    else{
      set1.foreach(triple => {
        if(!set2.exists(tri => tri.equals(triple))){
          result = false
        }
      })
    }
    result
  }
}
