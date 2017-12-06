package graphx

import java.io.{File, PrintWriter}

import cassandra.CassandraDB
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.{Map, Set}

/**
  * Created by user on 2017/5/25.
  */
object Justification_v2 {
  //vertex index
  var vindex:Long = 1L
  var eindex:Long = 1L
  var vertexMap:Map[(Long,Long,Long),Long] = Map()
  var searchMap:Map[Long,(Long,Long,Long)] = Map()
  var edgeMap:Map[(Long,Long,Long,Int),Long] = Map()
  var edgeSet:Set[Edge[Long]] = Set()
  var start:Long = 0L
  var end:Long = 0L
  var cost:Long = 0L
  var sub,pre,obj:Long = 0L
  var targetNum:Long = 0L

  def writeToTxt() = {
    var writer = new PrintWriter(new File("D:\\edges.txt"))
    for(edge <- edgeSet)
      writer.println(edge)
    writer.close()
    var writer1 = new PrintWriter(new File("D:\\vertex.txt"))
    vertexMap.foreach(a=>writer1.println(a._1+":"+a._2))
    writer1.close()
  }

  def main(args: Array[String]) {
//    println(args)
    var i:Int=0
    for(i <- 0 to args.length-1){
      if(args(i).equals("--subject"))
        sub = args(i+1).toLong
      if(args(i).equals("--predicate"))
        pre = args(i+1).toLong
      if(args(i).equals("--object"))
        obj = args(i+1).toLong
    }
    if(sub==0L && pre==0L && obj==0L){
      println("please input args --subject --predicate --object")
      return
    }
    var target = (sub,pre,obj)
    //屏蔽日志
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    //设置运行环境
    val conf = new SparkConf().setAppName("SimpleGraphX").setMaster("local")
    val sc = new SparkContext(conf)
    //get data from cassandra
    start = System.nanoTime()
    try{
      CassandraDB.connect()
      var resultrows = CassandraDB.session.execute("select * from mrjks.justifications;")
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
            val relation = getEdgeNumber((triple.rsubject,triple.rpredicate,triple.robject,triple._type.toInt))
            var verterFromNumber = getVertexNumber((singleTriple.subject,singleTriple.predicate,singleTriple._object))
            var edge = new Edge(verterFromNumber,vertexToNumber,relation)
            edgeSet.add(edge)
          }
        }
      }
    }finally {
      CassandraDB.close()
    }
//    writeToTxt()
    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("构建变和顶点共花费(s)："+cost)
    //构造vertexRDD和edgeRDD
    searchMap = vertexMap.map((x:((Long,Long,Long),Long)) => (x._2,x._1))
    //(Long,(Set[Set[Long]],Boolean,Boolean)):第一个Boolean用来表示是否和所求辩解集关联，第二个用来表示是否是原始三元组
    var vertexRDD: RDD[(Long,(Set[Set[Long]],Boolean,Boolean))] = sc.parallelize(searchMap.map((m:(Long,(Long,Long,Long)))=>(m._1,(Set(Set(-1L)),false,false))).toSeq)

    var edgeRDD: RDD[Edge[Long]] = sc.parallelize(edgeSet.toSeq)
    targetNum = vertexMap(target)
    println("targetNum："+targetNum)
    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("构建vertexRDD和edgeRDD过程共花费(s)："+cost)
    //构造图Graph[VD,ED]
    var graph: Graph[(Set[Set[Long]],Boolean,Boolean),Long] = Graph(vertexRDD,edgeRDD)
    //Pregel API
    val sourceId = targetNum
//    val initialGraph = graph.mapVertices((vid,vp)=>if(vid==sourceId) (vp._1,true) else (vp._1,false))
    var sssp = graph.pregel[(Map[Long,Map[Long,Set[(Long,Boolean)]]],Boolean)]((Map(),false),999,EdgeDirection.Both)(
      (id,dist,newDist) => {
        if(id == targetNum){
          dist
        }
        if(newDist._1.isEmpty && newDist._2){
          (dist._1,newDist._2,dist._3)
        }
        dist
//        else if(!newDist._2 && !(newDist._1.isEmpty)){
//          var results = dist._1
//          var news = newDist._1
//          for(key1 <- news.keySet){
//            if(key1 == id){
//              var result = news(key1)
//              for(key2 <- result.keySet){
//                results.add(result(key2))
//              }
//            }else{
//              for(temp <- results){
//                if(temp.contains(key1)){
//                  var _temp = temp
//                  _temp.remove(key1)
//                  results.remove(temp)
//                  var traversals = news(key1)
//                  for(key3 <- traversals.keySet){
//                    results.add(_temp ++ traversals(key3))
//                  }
//                }
//              }
//            }
//          }
//          (results,dist._2,dist._3)
//        }else { dist }
      },
      triple =>{
//        Iterator((targetNum,(Map(triple.dstId -> Map(triple.attr -> Set(triple.srcId))),false)))
        if(triple.dstAttr._2 && (!triple.srcAttr._2)){
          Iterator((triple.srcId,(Map(),true)))
//          Iterator((triple.dstId,(Map(triple.dstId -> Map(triple.attr -> Set(triple.srcId)),false))))
//        }else if(triple.dstAttr._2 && triple.srcAttr._2){
//          Iterator((targetNum,(Map(triple.dstId -> Map(triple.attr -> Set(triple.srcId))),false)))
        }else{
          Iterator.empty
        }
      },
      (a,b) => {
        var map1 = a._1
        var map2 = b._1
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
        (map1,a._2||b._2)
      }
    )
    sssp.vertices.filter(v => v._2._2).foreach(v => println(v._1+":"+v._2))
//    var vetex = sssp.vertices.filter(v => v._1==targetNum).foreach(v => {
//      var justifications = v._2._1
//      for(justification <- justifications){
//        justification.foreach(j=>print(searchMap(j)+" "))
//        println()
//      }
//    })

    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("求辩解之后(s)："+cost)
    sc.stop()
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
