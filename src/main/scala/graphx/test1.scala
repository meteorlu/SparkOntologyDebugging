package graphx
import org.apache.spark.{SparkConf, SparkContext}
import utils.Triple

import scala.collection.mutable._
/**
  * Created by user on 2017/11/6.
  */
object test1 {
  def main(args: Array[String]): Unit = {
      var sets = Set(1,2,3)
      var array1 = Array((sets,1))
      var array2 = Array.concat(array1,array1)
      var array3 = Array.concat(array1,array1,array1,array1,array1)
      array3.foreach(println(_))
//    val conf = new SparkConf().setAppName("GroupAndReduce").setMaster("local")
//    val sc = new SparkContext(conf)
//    var triple1 = new utils.Triple(1,1,1,false)
//    var triple11 = new utils.Triple(1,1,1,false)
//    var triple2 = new utils.Triple(2,2,2,false)
//    var triple3 = new utils.Triple(3,3,3,false)
//    var triple4 = new utils.Triple(4,4,4,false)
//    var triple5 = new utils.Triple(5,5,5,false)
//    var sets:Set[utils.Triple] = Set()
//    var sets1:Set[utils.Triple] = Set()
//    sets.add(triple1)
//    sets.add(triple3)
//    sets.add(triple2)
//    sets1.add(triple2)
//    sets1.add(triple11)
//    sets1.add(triple3)
//    println(sets)
//    println(sets1)
//    println(compareSetTriples(sets,sets1))
//    var list = Array((Set(triple2,triple1,triple3),1),(Set(triple3,triple1,triple2),1),(Set(triple3,triple2,triple1),1),
//                      (Set(triple2,triple3,triple4),1),(Set(triple2,triple4,triple3),1),(Set(triple4,triple3,triple2),0),
//                      (Set(triple3,triple4,triple5),1),(Set(triple4,triple3,triple5),0),(Set(triple4,triple5,triple3),0))
//    var wordRdd = sc.parallelize(list)
//    wordRdd.collect().foreach(x => println(x._2+":"+x._1))
//    var wordsCount = wordRdd.reduceByKey(_+_).collect().foreach(x => println(x._2+":"+x._1))
//    val words = Array("one", "two", "two", "three", "three", "three")
//    val wordsRDD = sc.parallelize(words).map(word => (word, 1))
//    val wordsCountWithReduce = wordsRDD.
//      reduceByKey(_ + _).
//      collect().
//      foreach(println)
//    val wordsCountWithGroup = wordsRDD.
//      groupByKey().
//      map(w => (w._1, w._2.sum)).
//      collect().
//      foreach(println)
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
