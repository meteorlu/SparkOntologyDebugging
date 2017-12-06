package utils

/**
  * Created by user on 2017/5/24.
  */
class Triple (sub:Long,pre:Long,obj:Long,typ:Long,rsu:Long,rpr:Long,rob:Long,isO:Boolean,triple:Triple) extends Serializable{
  var subject:Long = sub
  var predicate:Long = pre
  var _object:Long = obj
  var _type :Long= typ
  var rsubject:Long = rsu
  var rpredicate:Long = rpr
  var robject:Long= rob
  var isObjectLiteral:Boolean = isO
  def this(sub:Long,pre:Long,obj:Long,isO:Boolean){
    this(sub,pre,obj,0,0,0,0,isO,null)
  }
  def this(sub:Long,pre:Long,obj:Long,typ:Long,rsu:Long,rpr:Long,rob:Long){
    this(sub,pre,obj,typ,rsu,rpr,rob,false,null)
  }
  def this(triple:Triple){
    this(triple.subject,triple.predicate,triple._object,triple._type,triple.rsubject,triple.rpredicate,triple.robject,triple.isObjectLiteral,null)
  }
  override def toString:String={
    "(" + subject + " " + predicate + " " + _object + ")-" + _type + "->(" + rsubject + " " + rpredicate + " " + robject + ")"
  }

  def compareTo(o: Triple): Int = {
    if ((subject == o.subject) && (predicate == o.predicate) && (_object == o._object) && (_type == o._type)
      && (rsubject == o.rsubject) && (rpredicate == o.rpredicate) && (robject == o.robject))
      0
    else if (subject > o.subject) 1
          else {
            if (subject == o.subject) { // Check the predicate
              if (predicate > o.predicate) return 1
              else {
                if (predicate == o.predicate) { //Check the object
                  if (_object > o._object) return 1
                  else {
                    if (_object == o._object) { // check the type
                      if (_type > o._type) return 1
                      else {
                        if (_type == o._type) if (rsubject > o.rsubject) return 1
                        else {
                          if (rsubject == o.rsubject) if (rpredicate > o.rpredicate) return 1
                          else {
                            if (rpredicate == o.rpredicate) if (robject > o.robject) return 1
                            else return -1
                            return -1
                          }
                          return -1
                        }
                        return -1
                      }
                    }
                    return -1
                  }
                }
                return -1
              }
            }
            -1
          }
  }

  override def equals(triple: Any): Boolean = {
    if (compareTo(triple.asInstanceOf[Triple]) == 0) true
    else false
  }
}
