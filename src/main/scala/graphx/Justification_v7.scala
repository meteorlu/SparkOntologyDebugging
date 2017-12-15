package graphx

import java.io.{File, PrintWriter}

import cassandra.CassandraDB
import org.apache.log4j.{Level, Logger}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.{Map, Set, Stack}
import scala.io.Source

/**
  * Created by user on 2017/5/25.
  */
object Justification_v7 {
  //vertex index
  var vindex:Long = 1L
  var eindex:Long = 1L
  var vertexMap:Map[(Long,Long,Long),Long] = Map()
  var searchMap:Map[Long,(Long,Long,Long)] = Map()
  var edgeMap:Map[(Long,Long,Long,Int),Long] = Map()
  var edgeSet:Set[Edge[(Long,Boolean)]] = Set()
  var vertexStack:mutable.Stack[Long] = mutable.Stack()
  var maxDepth:Int = 0
  var start:Long = 0L
  var end:Long = 0L
  var cost:Long = 0L
  var sub,pre,obj:Long = 0L
  var targetNum:Long = 0L
  var dataRows:Set[utils.Triple] = Set()
  var resultSet:Set[(Array[String],Array[Set[Long]])] = Set()
  var resultSize:Long = 0L
  //garphX
  var vertexRDD: RDD[(Long,Boolean)] = null
  var edgeRDD: RDD[Edge[(Long,Boolean)]] = null
  //构造图Graph[VD,ED]
  var graph: Graph[Boolean, (Long,Boolean)] = null
  //  var oneStepMaps:Broadcast[Map[VertexId,Map[Long,Set[Long]]]] = null
  var isEnd:Broadcast[Boolean] = null

  def main(args: Array[String]) {
    findjustifications()
  }
  def findjustifications(): Unit ={
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    //设置运行环境
    val conf = new SparkConf()
      .setAppName("SimpleGraphX")
    var sc = new SparkContext(conf)
    try {
      CassandraDB.connect()
      var tripleMessage:Set[String] = Set()
//      var datarows = CassandraDB.session.execute("select * from mrjks.justifications limit 5000;")
//      import scala.collection.JavaConversions._
//      for (row <- datarows) {
//        var triple = new utils.Triple(row.getLong("sub"), row.getLong("pre"), row.getLong("obj"), false)
//        dataRows.add(triple)
//      }
      var fileSource = Source.fromFile("meteor/inputs.txt")
      for(line <- fileSource.getLines) {
        var strs = line.split("\t")
        sub = strs(0).toLong
        pre = strs(1).toLong
        obj = strs(2).toLong
        var triple = new utils.Triple(sub,pre,obj,false)
        dataRows.add(triple)
      }

      for (data <- dataRows) {
        //        --subject 8589955511 --predicate 0 --object 30064810538
        println("###########--subject " + data.subject + " --predicate " + data.predicate + " --object " + data._object)
        initParams()
        var target = (data.subject, data.predicate, data._object)
        start = System.nanoTime()
        //init vertext and edge
        initVertexAndEdge(new utils.Triple(target._1, target._2, target._3, false))
        println("最大深度:" + maxDepth)

        //判断是否有辩解
        if (vertexMap.size == 1 && edgeMap.isEmpty) {
          println("此三元组没有辩解")
          end = System.nanoTime()
          var results = Array(target.toString(), "0", ((end - start) / 1000000).toString)
          resultSet.add((results, Array()))
        } else {
          //构造vertexRDD和edgeRDD
          searchMap = vertexMap.map((x: ((Long, Long, Long), Long)) => (x._2, x._1))
          vertexRDD = sc.parallelize(searchMap.map((m: (Long, (Long, Long, Long))) => (m._1, true)).toSeq)
          edgeRDD = sc.parallelize(edgeSet.toSeq)
          targetNum = vertexMap(target)
          //构造图Graph[VD,ED]
          graph = Graph(vertexRDD, edgeRDD)
          //Pregel API
          import scala.collection.mutable.Map

          val graphOneStep = graph.aggregateMessages[Map[Long, Set[Long]]](
            e => {
              e.sendToDst(Map(e.attr._1 -> Set(e.srcId)))
            }, (a, b) => {
              var map1 = a
              var map2 = b
              for (key1 <- map2.keySet) {
                if (map1.contains(key1)) {
                  map1(key1) ++= map2(key1)
                } else {
                  map1 ++= Map(key1 -> map2(key1))
                }
              }
              a
            }
          ).collectAsMap()
          val oneStepMaps = sc.broadcast(graphOneStep)
          var initSet = Set(targetNum)
          var justifications: Array[Set[Long]] = Array(initSet)
          var lastjust: Array[Set[Long]] = Array()
          var step:Int = 0
          //求取辩解
          var resultRDD = sc.makeRDD(Seq(initSet))
          //          resultRDD.map(x=>(x,x.size)).reduce((x,y)=>(x._1,x._2+y._2))._2 > resultSize
          while (step < maxDepth) {
            step += 1
            resultRDD = sc.makeRDD(justifications)
            justifications = resultRDD.flatMap(x => {
              var results: Set[Set[Long]] = Set(x)
              //find one step justifications
              var tracingEntries: Map[Long, Set[Long]] = Map()
              x.foreach(vertex => {
                if (oneStepMaps.value.contains(vertex)) {
                  tracingEntries = oneStepMaps.value(vertex)
                  var temps: Set[Set[Long]] = results.clone()
                  for (temp <- temps) {
                    if (temp.contains(vertex)) {
                      for (tracing <- tracingEntries.keySet) {
                        var tracedTriples: Set[Long] = tracingEntries(tracing)
                        var _temp: Set[Long] = temp.clone()
                        _temp.remove(vertex)
                        _temp = _temp ++ tracedTriples
                        results.add(_temp)
                      }
                      results.remove(temp)
                    }
                  }
                }
              })
              results
            }).collect()

          }
          end = System.nanoTime()
          var results = Array(target.toString(),(justifications.size).toString,((end-start)/1000000).toString)
          resultSet.add((results,justifications))
        }
        var str = data.subject +"\t"+ data.predicate+"\t"+data._object+"\t"+maxDepth++"\t"+vertexMap.size+"\t"+edgeMap.size
        println(str)
        //        tripleMessage.add(str)
      }
      writeToResults(resultSet)
//      var writer = new PrintWriter(new File("D:\\tripleMessage.txt"))
//      tripleMessage.foreach(writer.println(_))
//      writer.close()
    }catch {
      case ex:Exception => println(ex.getMessage())
    }
    finally {
      CassandraDB.close()
    }
    sc.stop()
  }
  def initParams(): Unit = {
    vindex = 1L
    eindex = 1L
    vertexMap = Map()
    searchMap = Map()
    edgeMap = Map()
    edgeSet = Set()
    start = 0L
    end = 0L
    cost = 0L
    targetNum = 0L
    maxDepth = 0
    vertexStack = mutable.Stack()
  }
  /**
    *select data from cassandra to init the vertex and edge of the graph
    *
    * @param triple
    * @return
    */
  def initVertexAndEdge(triple:utils.Triple):Long={
    var resultrows = CassandraDB.session.execute("select * from mrjks.justifications where sub="+triple.subject+" and pre="+triple.predicate+" and obj="+triple._object+";")
    import scala.collection.JavaConversions._
    for (row <- resultrows) {
      var triple = new utils.Triple(
        row.getLong("sub"),
        row.getLong("pre"),
        row.getLong("obj"),
        row.getInt("rule"),
        row.getLong("v1"),
        row.getLong("v2"),
        row.getLong("v3"),
        row.getBool("isliteral"),
        null)
      var vertexTo = (triple.subject,triple.predicate,triple._object)
      var vertexToNumber:Long = getVertexNumber(vertexTo)

      vertexStack.push(vertexToNumber)
      if(vertexStack.size > maxDepth)
        maxDepth = vertexStack.size
      var tracingTriples:Set[utils.Triple] = utils.Util.tracing(triple)
      if(!tracingTriples.isEmpty){//it means the triple is not original
        for(singleTriple <- tracingTriples){
          val relation = (getEdgeNumber((triple.rsubject,triple.rpredicate,triple.robject,triple._type.toInt)),false)
          var verterFromNumber = getVertexNumber((singleTriple.subject,singleTriple.predicate,singleTriple._object))
          //检测vertexFromNumber是否已经存在栈中，存在说明有环
          if(vertexStack.exists(v => v == verterFromNumber)){
//            println("发现环路："+verterFromNumber+"->")
//            vertexStack.foreach(v =>print(v+"->"))
//            println()
          }else{
            //定点不可指向自己进行循环
            if(verterFromNumber != vertexToNumber){
              var edge = new Edge(verterFromNumber,vertexToNumber,relation)
              var edge_ = new Edge(vertexToNumber,verterFromNumber,relation)
              //            if(vertexMap.contains(verterFromNumber))
              if(!edgeSet.exists(e => e.srcId == vertexToNumber && e.dstId == verterFromNumber) && !edgeSet.contains(edge)) {
                //            if(!edgeSet.contains(edge)) {
                //              println(edge.toString())
                edgeSet.add(edge)
                initVertexAndEdge(singleTriple)
              }
            }
          }
        }
      }
      vertexStack.pop()
    }
    0
  }
  /**
    * get the vertexNumber
    * @param vertexTuple
    * @return
    */
  def getVertexNumber(vertexTuple:(Long,Long,Long)):Long = {
    //    var vertex = vertexMap.find((x:(Long,(Long,Long,Long))) => x._2.equals(vertexTuple))
    if(vertexMap.contains(vertexTuple))
      vertexMap(vertexTuple)
    else{
      var result = vindex
      vertexMap += (vertexTuple -> result)
      vindex += 1
      result
    }
  }

  /**
    * get the edgeNumber
    * @param edgeTuple
    * @return
    */
  def getEdgeNumber(edgeTuple:(Long,Long,Long,Int)):Long = {
    if(edgeMap.contains(edgeTuple))
      edgeMap(edgeTuple)
    else{
      var result = eindex
      edgeMap += (edgeTuple -> result)
      eindex += 1
      result
    }
  }

  /**
    * print result
    * @param results
    */
  def writeToResults(results:Set[(Array[String],Array[Set[Long]])]) =  {
    var writer = new PrintWriter(new File("meteor/resultsNewJust.txt"))
    results.foreach(r => {
      writer.println(r._1.apply(0)+"\t"+r._1.apply(1)+"\t"+r._1.apply(2))
//      r._2.foreach(println(_))
    })
    writer.close()
  }
}
