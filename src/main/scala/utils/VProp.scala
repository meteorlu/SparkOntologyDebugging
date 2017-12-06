package utils

import scala.collection.mutable.{Map,Set}

/**
  * Created by user on 2017/6/4.
  */
class VProp {
  var node:Long = 0
  var edge:Long = 0
  var isRelated:Boolean = false
  var results:Map[Long,Set[Set[Long]]] = Map()
  def combine(a: VProp,b: VProp){

  }
  def refreshVP(a: VProp,b: VProp): Unit ={
    if(!a.isRelated && !b.isRelated){
      //父节点传递给子节点，只改变标志
      a.isRelated = true
      b.isRelated = true
    }else if(a.isRelated && b.isRelated){
      //子节点传递给父节点，修改辩解集
      if(b.results.isEmpty){
//        if(a.results.contains(b.edge)) a.results(b.edge) = a.results(b.edge) ++ Set((b.node,b.isEnd))
//        else a.results = a.results ++ Map(b.edge -> Set((b.node,b.isEnd)))
      }else{
        //        for(key <- a.results.keySet){
        //          if(results.contains(key))
        //            results(key) = results(key) ++ vProp.results(key)
        //          else results = results ++ Map(key -> vProp.results(key))
        //        }
      }
    }
  }
}
