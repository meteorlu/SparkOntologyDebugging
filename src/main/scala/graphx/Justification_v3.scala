package graphx

import java.io.{File, PrintWriter}

import cassandra.CassandraDB
import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.Set

/**
  * Created by user on 2017/5/25.
  */
object Justification_v3 {
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
      //init vertext and edge
      initVertexAndEdge(new utils.Triple(sub,pre,obj,false));
    }finally {
      CassandraDB.close()
    }
//    writeToTxt()
    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("构建变和顶点共花费(s)："+cost)
    //构造vertexRDD和edgeRDD
    searchMap = vertexMap.map((x:((Long,Long,Long),Long)) => (x._2,x._1))
    var vertexRDD: RDD[(Long,Boolean)] = sc.parallelize(searchMap.map((m:(Long,(Long,Long,Long)))=>(m._1,false)).toSeq)
    var edgeRDD: RDD[Edge[(Long,Boolean)]] = sc.parallelize(edgeSet.toSeq)
    targetNum = vertexMap(target)

    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("构建vertexRDD和edgeRDD过程共花费(s)："+cost)
    //构造图Graph[VD,ED]
    var graph: Graph[Boolean, (Long,Boolean)] = Graph(vertexRDD,edgeRDD)
    //Pregel API
    val sourceId = targetNum
    val initialGraph = graph.mapVertices((vid,_)=>if(vid==sourceId) true else false)
    var sssp = initialGraph.pregel(false,999,EdgeDirection.Both)(
      (id,dist,newDist) => (dist||newDist),
      triple =>{
        if(triple.dstAttr&&(!triple.srcAttr)){
          Iterator((triple.srcId,triple.dstAttr))
        }else{
          Iterator.empty
        }
      },
      (a,b) => (a||b)
    )
//    sssp.vertices.filter(v=>v._2).foreach(v=>println(v))
//    val subgraph = sssp.subgraph(vpred = (vid,tag)=>tag)
    import scala.collection.mutable.Map
    var oneStepMaps:Map[Long,Map[Long,Set[Long]]] = sssp.subgraph(vpred = (vid,tag)=>tag).edges.map(e => Map(e.dstId -> Map(e.attr._1 -> Set(e.srcId))))
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

    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("所有构建过程共花费(s)："+cost)

    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("求辩解前(s)："+cost)
    var justifications = findjustifications(targetNum,oneStepMaps)
    for(justification <- justifications){
      justification.foreach(j=>print(searchMap(j)+" "))
      println()
    }
    end = System.nanoTime()
    cost = (end - start)/1000000000
    println("求辩解之后(s)："+cost)
    sc.stop()
  }

  /**
    *select data from cassandra to init the vertex and edge of the graph
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
          var edge = new Edge(verterFromNumber,vertexToNumber,relation)
          edgeSet.add(edge)
          initVertexAndEdge(singleTriple)
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
  /**
    * find a triple's justifications
    * @return
    */
  import scala.collection.mutable.Map
  def findjustifications(vertex:Long, oneStepMaps:Map[Long,Map[Long,Set[Long]]]):Set[Set[Long]] = {
    var results:Set[Set[Long]] = Set()
    //find one step justifications
    var tracingEntries:Map[Long,Set[Long]] = Map()
    if(oneStepMaps.contains(vertex)) tracingEntries = oneStepMaps(vertex)
//    try{
//      tracingEntries = subgraph.triplets.
//        filter(triplet => triplet.dstId == vertex)
//        .map(triple => Map(triple.attr._1 -> Set(triple.srcId)))
//        .reduce((a:Map[Long,Set[Long]],b:Map[Long,Set[Long]])
//        => {for(item <- b.keySet){
//          if(a.contains(item)){
//            a(item)=a(item)++b(item)
//          }else{
//            a += (item -> b(item))
//          }
//        }
//          a})
//    }catch {
//      case e:UnsupportedOperationException => results
//      case _:Exception => results
//    }
    if(tracingEntries.isEmpty) results
    for(tracing <- tracingEntries.keySet){
      var tracedTriples:Set[Long] = tracingEntries(tracing)
      results.add(tracedTriples)
      var temps:Set[Set[Long]] = results
      for(singleTriple <- tracedTriples){
        var traversals:Set[Set[Long]] = findjustifications(singleTriple,oneStepMaps)
        if(traversals.size != 0){
          for(temp <- temps){
            if(temp.contains(singleTriple)){
              for(traversal <- traversals){
                var _temp:Set[Long] = temp
                _temp.remove(singleTriple)
                _temp=_temp++traversal
                results.remove(temp)
                results.add(_temp)
              }
            }
          }
        }
      }
    }
    results
  }
}
