package graphx

import java.io.{File, PrintWriter}

import cassandra.CassandraDB
import org.apache.log4j.{Level, Logger}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import utils.Triple

import scala.collection.mutable.{Map, Set}

/**
  * Created by user on 2017/5/25.
  */
object Justification_v6 {
  //vertex index
  var vindex:Long = 1L
  var eindex:Long = 1L
  var vertexMap:Map[(Long,Long,Long),Long] = Map()
  var searchMap:Map[Long,(Long,Long,Long)] = Map()
  var edgeMap:Map[(Long,Long,Long,Int),Long] = Map()
  var edgeSet:Set[Edge[(Long,Boolean)]] = Set()
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
  var oneStepMaps:Broadcast[Map[Long,Map[Long,Set[Long]]]] = null
  var isEnd:Broadcast[Boolean] = null

  def main(args: Array[String]) {
    findjustifications()
  }
  def writeToTxt() = {
    var writer = new PrintWriter(new File("D:\\edges.txt"))
    for(edge <- edgeSet)
      writer.println(edge)
    writer.close()
    var writer1 = new PrintWriter(new File("D:\\vertex.txt"))
    vertexMap.foreach(a=>writer1.println(a._1+":"+a._2))
    writer1.close()
  }

  def writeToResults(results:Set[(Array[String],Array[Set[Long]])]) =  {
    var writer = new PrintWriter(new File("D:\\results6.txt"))
    results.foreach(r => {
      writer.println(r._1.apply(0)+"\t"+r._1.apply(1)+"\t"+r._1.apply(2))
      writer.println("Justifications:")
      r._2.foreach(r => writer.println(r))
    })
    writer.close()
  }
  def findjustifications(): Unit ={
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
    //设置运行环境
    val conf = new SparkConf()
      .setAppName("SimpleGraphX")
      .setMaster("local")
    val sc = new SparkContext(conf)
    try{
      CassandraDB.connect()
//      var datarows = CassandraDB.session.execute("select * from mrjks.justifications limit 5;")
//      import scala.collection.JavaConversions._
//      for (row <- datarows) {
//        var triple = new utils.Triple(row.getLong("sub"),row.getLong("pre"),row.getLong("obj"),false)
//        dataRows.add(triple)
//      }
      dataRows.add(new utils.Triple(579225L,0,34359751031L,false))
      for (data <- dataRows){
//        --subject 8589955511 --predicate 0 --object 30064810538
        println("--subject "+data.subject+" --predicate "+data.predicate+" --object "+data._object)
        initParams()
        var target = (data.subject,data.predicate,data._object)
        start = System.nanoTime()
        //init vertext and edge
        initVertexAndEdge(new utils.Triple(target._1,target._2,target._3,false))
        //判断是否有辩解
        if(vertexMap.size == 1 && edgeMap.isEmpty){
          println("此三元组没有辩解")
          end = System.nanoTime()
          var results = Array(target.toString(),"0",((end-start)/1000000).toString)
          resultSet.add((results,Array()))
        }else{
          //构造vertexRDD和edgeRDD
          searchMap = vertexMap.map((x:((Long,Long,Long),Long)) => (x._2,x._1))
          vertexRDD = sc.parallelize(searchMap.map((m:(Long,(Long,Long,Long)))=>(m._1,true)).toSeq)
          edgeRDD = sc.parallelize(edgeSet.toSeq)
          targetNum = vertexMap(target)
          //构造图Graph[VD,ED]
          graph = Graph(vertexRDD,edgeRDD)
          //Pregel API
          import scala.collection.mutable.Map
          val oneStep:Map[Long,Map[Long,Set[Long]]] =
            graph.edges
              .map(e => Map(e.dstId -> Map(e.attr._1 -> Set(e.srcId))))
              .reduce((a:Map[Long,Map[Long,Set[Long]]],b:Map[Long,Map[Long,Set[Long]]])
              => {
                var map1 = a
                var map2 = b
                for(key1 <- map2.keySet){
                  if(map1.contains(key1)){
                    var map1_1 = map1(key1)
                    var map2_2 = map2(key1)
                    for(key2 <- map2_2.keySet){
                      if(map1_1.contains(key2)){
                        map1_1(key2) ++= map2_2(key2)
                      }else{
                        map1_1 ++= Map(key2 -> map2_2(key2))
                      }
                    }
                    map1(key1) = map1_1
                  }else{
                    map1 ++= Map(key1 -> map2(key1))
                  }
                }
                a
              }
              )
          oneStepMaps = sc.broadcast(oneStep)
          var initSet = Set(targetNum)
          var justifications:Array[Set[Long]] = Array()
          //求取辩解
          var resultRDD = sc.makeRDD(Seq(initSet))
          while (resultRDD.map(x=>(x,x.size)).reduce((x,y)=>(x._1,x._2+y._2))._2 > resultSize){
            resultSize = resultRDD.map(x=>(x,x.size)).reduce((x,y)=>(x._1,x._2+y._2))._2
            justifications = resultRDD.flatMap(x => {
              var results:Set[Set[Long]] = Set(x)
              //find one step justifications
              var tracingEntries:Map[Long,Set[Long]] = Map()
              x.foreach(vertex =>{
                if(oneStepMaps.value.contains(vertex)){
                  tracingEntries = oneStepMaps.value(vertex)
                  var temps:Set[Set[Long]] = results.clone()
                  for(temp <- temps){
                    if(temp.contains(vertex)){
                      for(tracing <- tracingEntries.keySet){
                        var tracedTriples:Set[Long] = tracingEntries(tracing)
                        var _temp:Set[Long] = temp.clone()
                        _temp.remove(vertex)
                        _temp = _temp++tracedTriples
                        results.add(_temp)
                      }
                      results.remove(temp)
                    }
                  }
                }
              })
              results
            }).collect()
            resultRDD = sc.makeRDD(justifications)
          }

          end = System.nanoTime()
          var results = Array(target.toString(),(justifications.size).toString,((end-start)/1000000).toString)
          resultSet.add((results,justifications))
          println(resultSet.size)
        }
      }
      writeToResults(resultSet)
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

      var tracingTriples:Set[utils.Triple] = utils.Util.tracing(triple)
      if(!tracingTriples.isEmpty){//it means the triple is not original
        for(singleTriple <- tracingTriples){
          val relation = (getEdgeNumber((triple.rsubject,triple.rpredicate,triple.robject,triple._type.toInt)),false)
          var verterFromNumber = getVertexNumber((singleTriple.subject,singleTriple.predicate,singleTriple._object))
          //定点不可指向自己进行循环
          if(verterFromNumber != vertexToNumber){
            var edge = new Edge(verterFromNumber,vertexToNumber,relation)
            var edge_ = new Edge(vertexToNumber,verterFromNumber,relation)
            //            if(vertexMap.contains(verterFromNumber))
            //            if(!edgeSet.exists(e => e.srcId == vertexToNumber && e.dstId == verterFromNumber) && !edgeSet.contains(edge)) {
            if(!edgeSet.contains(edge)) {
              println(edge.toString())
              edgeSet.add(edge)
              initVertexAndEdge(singleTriple)
            }
          }
        }
      }
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
}
