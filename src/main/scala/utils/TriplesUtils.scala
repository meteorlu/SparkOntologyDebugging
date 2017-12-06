package utils

/**
  * Created by user on 2017/5/24.
  */
object TriplesUtils {

  /** *** RDFS 规则号 *****/
  val RDFS_NA = 0xf0 // 不可用的rule

  val RDFS_1 = 0xf1
  val RDFS_2 = 0xf2
  val RDFS_3 = 0xf3
  val RDFS_4a = 0xf4a
  val RDFS_4b = 0xf4b
  val RDFS_5 = 0xf5
  val RDFS_6 = 0xf6
  val RDFS_7 = 0xf7
  val RDFS_8 = 0xf8
  val RDFS_9 = 0xf9
  val RDFS_10 = 0xf10
  val RDFS_11 = 0xf11
  val RDFS_12 = 0xf12
  val RDFS_13 = 0xf13

  /** *** OWL Horst 规则标号 *****/
  val OWL_HORST_NA = 0x0
  val OWL_HORST_1 = 0x1
  val OWL_HORST_2 = 0x2
  val OWL_HORST_3 = 0x3
  val OWL_HORST_4 = 0x4
  val OWL_HORST_5a = 0x5a
  val OWL_HORST_5b = 0x5b
  val OWL_HORST_6 = 0x6
  val OWL_HORST_7 = 0x7
  val OWL_HORST_8 = 0x8 //很难判断是8a或者8b，因此用一个统称

  val OWL_HORST_8a = 0x8a
  val OWL_HORST_8b = 0x8b
  val OWL_HORST_9 = 0x9
  val OWL_HORST_10 = 0x10
  val OWL_HORST_11 = 0x11
  //	public static final long OWL_HORST_12ab = 0x12ab;	//很难判断是12a或者12b，因此用一个统称
  val OWL_HORST_12a = 0x12a
  val OWL_HORST_12b = 0x12b
  val OWL_HORST_12c = 0x12c
  //	public static final long OWL_HORST_13ab = 0x13ab;	//很难判断是13a或者13b，因此用一个统称
  val OWL_HORST_13a = 0x13a
  val OWL_HORST_13b = 0x13b
  val OWL_HORST_13c = 0x13c
  val OWL_HORST_14a = 0x14a
  val OWL_HORST_14b = 0x14b
  val OWL_HORST_15 = 0x15
  val OWL_HORST_16 = 0x16
  // Added by Wugang 20150108, same as synonyms table, related to 5,6,7,9,10.11
  val OWL_HORST_SYNONYMS_TABLE = 0x56791011


  /** *** Standard URIs IDs *****/
  val RDF_TYPE = 0
  val RDF_PROPERTY = 1
  val RDFS_RANGE = 2
  val RDFS_DOMAIN = 3
  val RDFS_SUBPROPERTY = 4
  val RDFS_SUBCLASS = 5
  val RDFS_MEMBER = 19
  val RDFS_LITERAL = 20
  val RDFS_CONTAINER_MEMBERSHIP_PROPERTY = 21
  val RDFS_DATATYPE = 22
  val RDFS_CLASS = 23
  val RDFS_RESOURCE = 24
  val OWL_CLASS = 6
  val OWL_FUNCTIONAL_PROPERTY = 7
  val OWL_INVERSE_FUNCTIONAL_PROPERTY = 8
  val OWL_SYMMETRIC_PROPERTY = 9
  val OWL_TRANSITIVE_PROPERTY = 10
  val OWL_SAME_AS = 11
  val OWL_INVERSE_OF = 12
  val OWL_EQUIVALENT_CLASS = 13
  val OWL_EQUIVALENT_PROPERTY = 14
  val OWL_HAS_VALUE = 15
  val OWL_ON_PROPERTY = 16
  val OWL_SOME_VALUES_FROM = 17
  val OWL_ALL_VALUES_FROM = 18

  /** *** Standard URIs *****/
  val S_RDF_TYPE = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>"
  val S_RDF_PROPERTY = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>"
  val S_RDFS_RANGE = "<http://www.w3.org/2000/01/rdf-schema#range>"
  val S_RDFS_DOMAIN = "<http://www.w3.org/2000/01/rdf-schema#domain>"
  val S_RDFS_SUBPROPERTY = "<http://www.w3.org/2000/01/rdf-schema#subPropertyOf>"
  val S_RDFS_SUBCLASS = "<http://www.w3.org/2000/01/rdf-schema#subClassOf>"
  val S_RDFS_MEMBER = "<http://www.w3.org/2000/01/rdf-schema#member>"
  val S_RDFS_LITERAL = "<http://www.w3.org/2000/01/rdf-schema#Literal>"
  val S_RDFS_CONTAINER_MEMBERSHIP_PROPERTY = "<http://www.w3.org/2000/01/rdf-schema#ContainerMembershipProperty>"
  val S_RDFS_DATATYPE = "<http://www.w3.org/2000/01/rdf-schema#Datatype>"
  val S_RDFS_CLASS = "<http://www.w3.org/2000/01/rdf-schema#Class>"
  val S_RDFS_RESOURCE = "<http://www.w3.org/2000/01/rdf-schema#Resource>"
  val S_OWL_CLASS = "<http://www.w3.org/2002/07/owl#Class>"
  val S_OWL_FUNCTIONAL_PROPERTY = "<http://www.w3.org/2002/07/owl#FunctionalProperty>"
  val S_OWL_INVERSE_FUNCTIONAL_PROPERTY = "<http://www.w3.org/2002/07/owl#InverseFunctionalProperty>"
  val S_OWL_SYMMETRIC_PROPERTY = "<http://www.w3.org/2002/07/owl#SymmetricProperty>"
  val S_OWL_TRANSITIVE_PROPERTY = "<http://www.w3.org/2002/07/owl#TransitiveProperty>"
  val S_OWL_SAME_AS = "<http://www.w3.org/2002/07/owl#sameAs>"
  val S_OWL_INVERSE_OF = "<http://www.w3.org/2002/07/owl#inverseOf>"
  val S_OWL_EQUIVALENT_CLASS = "<http://www.w3.org/2002/07/owl#equivalentClass>"
  val S_OWL_EQUIVALENT_PROPERTY = "<http://www.w3.org/2002/07/owl#equivalentProperty>"
  val S_OWL_HAS_VALUE = "<http://www.w3.org/2002/07/owl#hasValue>"
  val S_OWL_ON_PROPERTY = "<http://www.w3.org/2002/07/owl#onProperty>"
  val S_OWL_SOME_VALUES_FROM = "<http://www.w3.org/2002/07/owl#someValuesFrom>"
  val S_OWL_ALL_VALUES_FROM = "<http://www.w3.org/2002/07/owl#allValuesFrom>"

  /** *** TRIPLES TYPES *****/
  //USED IN RDFS REASONER
  val DATA_TRIPLE = 0
  val DATA_TRIPLE_TYPE = 5
  val SCHEMA_TRIPLE_RANGE_PROPERTY = 1
  val SCHEMA_TRIPLE_DOMAIN_PROPERTY = 2
  val SCHEMA_TRIPLE_SUBPROPERTY = 103 // Old value is 3,  We cluster SC SP EC EP, Since OWLEquivalenceSCSPMapper require input from

  val SCHEMA_TRIPLE_MEMBER_SUBPROPERTY = 23 // Old value is 20 which conflicts with TRANSITIVE_TRIPLE = 20 !!!!

  val SCHEMA_TRIPLE_SUBCLASS = 104 // Old value is 4, We cluster SC SP EC EP, Since OWLEquivalenceSCSPMapper require input from

  val SCHEMA_TRIPLE_RESOURCE_SUBCLASS = 21
  val SCHEMA_TRIPLE_LITERAL_SUBCLASS = 22

  //USED FOR OWL REASONER
  val SCHEMA_TRIPLE_FUNCTIONAL_PROPERTY = 6
  val SCHEMA_TRIPLE_INVERSE_FUNCTIONAL_PROPERTY = 7
  val SCHEMA_TRIPLE_SYMMETRIC_PROPERTY = 8
  val SCHEMA_TRIPLE_TRANSITIVE_PROPERTY = 9
  val DATA_TRIPLE_SAME_AS = 10
  val SCHEMA_TRIPLE_INVERSE_OF = 11
  val DATA_TRIPLE_CLASS_TYPE = 12
  val DATA_TRIPLE_PROPERTY_TYPE = 13
  val SCHEMA_TRIPLE_EQUIVALENT_CLASS = 114 // Old 14, We cluster SC SP EC EP, Since OWLEquivalenceSCSPMapper require input from

  val SCHEMA_TRIPLE_EQUIVALENT_PROPERTY = 115 // Old 15, We cluster SC SP EC EP, Since OWLEquivalenceSCSPMapper require input from

  val DATA_TRIPLE_HAS_VALUE = 16
  val SCHEMA_TRIPLE_ON_PROPERTY = 17
  val SCHEMA_TRIPLE_SOME_VALUES_FROM = 18
  val SCHEMA_TRIPLE_ALL_VALUES_FROM = 19
  val TRANSITIVE_TRIPLE = 20
  val SYNONYMS_TABLE = 56791011

  //FILE PREFIXES
  val DIR_PREFIX = "dir-"
  val OWL_PREFIX = "owl-"

  //FILE SUFFIXES
  val FILE_SUFF_OTHER_DATA = "-other-data"
  val FILE_SUFF_RDF_TYPE = "-type-data"
  val FILE_SUFF_OWL_SYMMETRIC_TYPE = "-symmetric-property-type-data"
  val FILE_SUFF_OWL_TRANSITIVE_TYPE = "-transitive-property-type-data"
  val FILE_SUFF_OWL_CLASS_TYPE = "-rdfs-class-type-data"
  val FILE_SUFF_OWL_PROPERTY_TYPE = "-owl-property-type-data"
  val FILE_SUFF_OWL_FUNCTIONAL_PROPERTY_TYPE = "-functional-property-type-data"
  val FILE_SUFF_OWL_INV_FUNCTIONAL_PROPERTY_TYPE = "-inverse-functional-property-type-data"

  val FILE_SUFF_RDFS_SUBCLASS = "-subclas-schema"
  val FILE_SUFF_RDFS_RESOURCE_SUBCLASS = "-resource-subclas-schema"
  val FILE_SUFF_RDFS_LITERAL_SUBCLASS = "-literal-subclas-schema"
  val FILE_SUFF_RDFS_SUBPROP = "-subprop-schema"
  val FILE_SUFF_RDFS_DOMAIN = "-domain-schema"
  val FILE_SUFF_RDFS_RANGE = "-range-schema"
  val FILE_SUFF_RDFS_MEMBER_SUBPROP = "-member-subprop-schema"
  val FILE_SUFF_OWL_SAME_AS = "-same-as-data"
  val FILE_SUFF_OWL_INVERSE_OF = "-inverse-of-schema"
  val FILE_SUFF_OWL_EQUIVALENT_CLASS = "-equivalent-class-schema"
  val FILE_SUFF_OWL_EQUIVALENT_PROPERTY = "-equivalent-property-schema"
  val FILE_SUFF_OWL_HAS_VALUE = "-has-value-data"
  val FILE_SUFF_OWL_ON_PROPERTY = "-on-property-schema"
  val FILE_SUFF_OWL_SOME_VALUES = "-some-values-schema"
  val FILE_SUFF_OWL_ALL_VALUES = "-all-values-schema"
}
