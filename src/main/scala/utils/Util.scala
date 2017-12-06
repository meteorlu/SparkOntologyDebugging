package utils

import scala.collection.mutable.Set
/**
  * Created by user on 2017/5/24.
  */
object Util {
  def tracing(triple: Triple):Set[Triple] = {
    val tracedTriples:Set[Triple] = Set()
    val _type = triple._type
    _type match {
      case TriplesUtils.OWL_HORST_1 =>
      // p rdf:type owl:FunctionalProperty, [u p v], u p w => v owl:sameAs w
      // <v, owl:sameAs, w, OWL_HORST_1, u, p, v>
    val triple1: Triple = new Triple (triple.rpredicate, TriplesUtils.RDF_TYPE, TriplesUtils.OWL_FUNCTIONAL_PROPERTY, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    val triple3: Triple = new Triple (triple.rsubject, triple.rpredicate, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_2 =>
      // p rdf:type owl:InverseFunctionalProperty, [v p u], w p u => v owl:sameAs w
      // <v owl:sameAs, w, OWL_HORST_2, v, p, u>
    val triple1: Triple = new Triple (triple.rpredicate, TriplesUtils.RDF_TYPE, TriplesUtils.OWL_INVERSE_FUNCTIONAL_PROPERTY, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    val triple3: Triple = new Triple (triple._object, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_3 =>
      // p rdf:type owl:SymmetricProperty, [v p u] => u p v
      // <u, p, v, OWL_HORST_3, v, p, u>
    val triple1: Triple = new Triple (triple.predicate, TriplesUtils.RDF_TYPE, TriplesUtils.OWL_SYMMETRIC_PROPERTY, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_4 =>
      // p rdf:type owl:TransitiveProperty, [u p w], w p v => u p v
      // <u, p, v, OWL_HORST_4, u, p, w>
    val triple1: Triple = new Triple (triple.predicate, TriplesUtils.RDF_TYPE, TriplesUtils.OWL_TRANSITIVE_PROPERTY, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    val triple3: Triple = new Triple (triple.robject, triple.predicate, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)
      //			System.out.println("I'm OWL_HORST_4: " + triple + derivedTriples);

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_5a =>
    //log.error ("OWL Horst Rule 5a is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_5b =>
    //log.error ("OWL Horst Rule 5b is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_6 =>
    //log.error ("OWL Horst Rule 6 is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_7 =>
      // [v owl:sameAs w], w owl:sameAs u => v owl:sameAs u
      // <v, owl:sameAs, u, OWL_HORST_7, v, owl:sameAs, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_SAME_AS, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.OWL_SAME_AS, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_8 =>
      // p owl:inverseOf q, [v p w] => w q v  // OWL_HORST_8a
      // <w, q, v, OWL_HORST_8, v, p, w>
      // p owl:inverseOf q, [v q w] => w p v  // OWL_HORST-8b
      // <w, p, v, OWL_HORST_8, v, q, w>
    val triple1: Triple = new Triple (triple.rpredicate, TriplesUtils.OWL_INVERSE_OF, triple.predicate, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_8a =>
    //log.error ("OWL Horst Rule 8a is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_8b =>
    //log.error ("OWL Horst Rule 8b is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_9 =>
    //log.error ("OWL Horst Rule 9 is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_10 =>
    //log.error ("OWL Horst Rule 10 is not implemented!")
     //todo:  is not supported
      case TriplesUtils.OWL_HORST_11 =>
      // [u p v], u owl:sameAs x, v owl:sameAs y => x p y
      // <x, p, y, OWL_HORST_11, u, p, v>
    val triple1: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    val triple2: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_SAME_AS, triple.subject, false)
    val triple3: Triple = new Triple (triple.robject, TriplesUtils.OWL_SAME_AS, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_12a =>
      // [v owl:equivalentClass w] => v rdfs:subClassOf w
      // <v, rdfs:subClassOf, w, OWL_HORST_12a, v, owl:equivalentClass, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_EQUIVALENT_CLASS, triple.robject, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_12b =>
      // [v owl:equivalentClass w] => w rdfs:subClassOf v
      // <w, rdfs:subClassOf, v, OWL_HORST_12b, v, owl:equivalentClass, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_EQUIVALENT_CLASS, triple.robject, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_12c =>
      // [v rdfs:subClassOf w], w rdfs:subClassOf v => v rdfs:equivalentClass w
      // <v, rdfs:equivalentClass, w, OWL_HORST_12c, v, rdfs:subClassOf, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.RDFS_SUBCLASS, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.RDFS_SUBCLASS, triple.rsubject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_13a =>
      // [v owl:equivalentProperty w] => v rdfs:subPropertyOf w
      // <v, rdfs:subPropertyOf, w, OWL_HORST_13a, v, owl:equivalentProperty, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_EQUIVALENT_PROPERTY, triple.robject, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_13b =>
      // [v owl:equivalentProperty w] => w rdfs:subPropertyOf v
      // <w, rdfs:subPropertyOf, v, OWL_HORST_13b, v, owl:equivalentProperty, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_EQUIVALENT_PROPERTY, triple.robject, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_13c =>
      // [v rdfs:subPropertyOf w], w rdfs:subPropertyOf v => v rdfs:equivalentProperty w
      // <v, rdfs:equivalentProperty, w, OWL_HORST_13c, v, rdfs:subPropertyOf, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.RDFS_SUBPROPERTY, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.RDFS_SUBPROPERTY, triple.rsubject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_14a =>
      // [v] owl:hasValue [w], v owl:onProperty [p], u p w => u rdf:type v
      // <u, rdf:type, v, OWL_HORST_14a, v, p, w>
    val triple1: Triple = new Triple (triple._object, TriplesUtils.OWL_HAS_VALUE, triple.robject, false)
    val triple2: Triple = new Triple (triple._object, TriplesUtils.OWL_ON_PROPERTY, triple.rpredicate, false)
    val triple3: Triple = new Triple (triple.subject, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_14b =>
      // [v owl:hasValue w], v owl:onProperty p, u rdf:type v => u p w
      // <u, p, w, OWL_HORST_14b, v, owl:hasValue, w>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_HAS_VALUE, triple.robject, false)
    val triple2: Triple = new Triple (triple.rsubject, TriplesUtils.OWL_ON_PROPERTY, triple.predicate, false)
    val triple3: Triple = new Triple (triple.subject, TriplesUtils.RDF_TYPE, triple.rsubject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_15 =>
      // v owl:someValuesFrom w, v owl:onProperty [p], u p x, [x] rdf:type [w] => u rdf:type v
      // <u, rdf:type, v, OWL_HORST_15, x, p, w>
    val triple1: Triple = new Triple (triple._object, TriplesUtils.OWL_SOME_VALUES_FROM, triple.robject, false)
    val triple2: Triple = new Triple (triple._object, TriplesUtils.OWL_ON_PROPERTY, triple.rpredicate, false)
    val triple3: Triple = new Triple (triple.subject, triple.rpredicate, triple.rsubject, false)
    val triple4: Triple = new Triple (triple.rsubject, TriplesUtils.RDF_TYPE, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)
    tracedTriples.add (triple4)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_16 =>
      // v owl:allValuesFrom u, v owl:onProperty [p], [w] rdf:type [v], w p x => x rdf:type u
      // <x, rdf:type, u, OWL_HORST_16, w, p, v>
    val triple1: Triple = new Triple (triple.robject, TriplesUtils.OWL_ALL_VALUES_FROM, triple._object, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.OWL_ON_PROPERTY, triple.rpredicate, false)
    val triple3: Triple = new Triple (triple.rsubject, TriplesUtils.RDF_TYPE, triple.robject, false)
    val triple4: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.subject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)
    tracedTriples.add (triple3)
    tracedTriples.add (triple4)
      //			System.out.println("I'm OWL_HORST_16: " + triple + derivedTriples);

     //todo:  is not supported
      case TriplesUtils.RDFS_NA =>
    //log.error ("This RDFS rule is not implemented!")
     //todo:  is not supported
      case TriplesUtils.RDFS_1 =>
    //log.error ("RDFS Rule 1 is not implemented!")
     //todo:  is not supported
      case TriplesUtils.RDFS_2 =>
      // p rdfs:domain x, [s p o] => s rdf:type x
      // <s, rdf:type, x, RDFS_2, s, p, o>
    val triple1: Triple = new Triple (triple.rpredicate, TriplesUtils.RDFS_DOMAIN, triple._object, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_3 =>
      // p rdfs:range x, [s p o] => o rdf:type x
      // <o, rdf:type, x, RDFS_3, s, p, o>
    val triple1: Triple = new Triple (triple.rpredicate, TriplesUtils.RDFS_RANGE, triple._object, false)
    val triple2: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_4a =>
    //log.error ("RDFS Rule 4a is not implemented!")
     //todo:  is not supported
      case TriplesUtils.RDFS_4b =>
    //log.error ("RDFS Rule 4b is not implemented!")
     //todo:  is not supported
      case TriplesUtils.RDFS_5 =>
      // [p rdfs:subPropertyOf q], q rdfs:subPropertyOf r => p rdfs:subPropertyOf r
      // <p, rdfs:subPropertyOf, r, RDFS_5, p, rdfs:subPropertyOf, q>
    val triple1: Triple = new Triple (triple.subject, TriplesUtils.RDFS_SUBPROPERTY, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.RDFS_SUBPROPERTY, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_6 =>
    //log.error ("RDFS Rule 6 is not implemented!")
     //todo:  is not supported
      case TriplesUtils.RDFS_7 =>
      // [s p o], p rdfs:subPropertyOf q => s q o
      // <s, q, o, RDFS_7, s, p, o>
    val triple1: Triple = new Triple (triple.rsubject, triple.rpredicate, triple.robject, false)
    val triple2: Triple = new Triple (triple.rpredicate, TriplesUtils.RDFS_SUBPROPERTY, triple.predicate, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_8 =>
      // [s rdf:type rdfs:Class] => s rdfs:subClassOf rdfs:Resource
      // <s, rdfs:subClassOf, rdfs:Resource, RDFS_8, s, rdf:type, rdfs:Class>
    val triple1: Triple = new Triple (triple.subject, TriplesUtils.RDF_TYPE, TriplesUtils.RDFS_CLASS, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.RDFS_9 =>
      // [s rdf:type x], x rdfs:subClassOf y => s rdf:type y
      // <s, rdf:type, y, RDFS_9, s, rdf:type, x>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.RDF_TYPE, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.RDFS_SUBCLASS, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_10 =>
    ////log.error ("RDFS Rule 10 is not implemented!")
    //todo:  is not supported
      case TriplesUtils.RDFS_11 =>
      // [x rdfs:subClassOf y], y rdfs:subClassOf z => x rdfs:subClassOf z
      // <x, rdfs:subClassOf, z, RDFS_11, x, rdfs:subClassOf, y>
    val triple1: Triple = new Triple (triple.rsubject, TriplesUtils.RDFS_SUBCLASS, triple.robject, false)
    val triple2: Triple = new Triple (triple.robject, TriplesUtils.RDFS_SUBCLASS, triple._object, false)
    tracedTriples.add (triple1)
    tracedTriples.add (triple2)

     //todo:  is not supported
      case TriplesUtils.RDFS_12 =>
      // [p rdf:type rdfs:ContainerMembershipProperty] => p rdfs:subPropertyOf rdfs:member
      // <p, rdfs:subPropertyOf, rdfs:member, RDFS_12, p, rdf:type, rdfs:ContainerMembershipProperty>
    val triple1: Triple = new Triple (triple.subject, TriplesUtils.RDF_TYPE, TriplesUtils.RDFS_CONTAINER_MEMBERSHIP_PROPERTY, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.RDFS_13 =>
      // [o rdf:type rdfs:Datatype] => o rdfs:subClassOf rdfs:Literal
      // <o, rdfs:subClassOf, rdfs:Literal, RDFS_13, o, rdf:type, rdfs:Datatype>
    val triple1: Triple = new Triple (triple.subject, TriplesUtils.RDF_TYPE, TriplesUtils.RDFS_DATATYPE, false)
    tracedTriples.add (triple1)

     //todo:  is not supported
      case TriplesUtils.OWL_HORST_NA =>
      case _ =>
        ////log.error("This OWL Horst rule is not implemented!")
         //todo:  is not supported
    }
    tracedTriples
  }
}
