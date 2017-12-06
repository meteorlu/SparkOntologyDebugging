package cassandra

import com.datastax.driver.core.{Cluster, Session}

/**
  * Created by user on 2017/5/23.
  */
object CassandraDB {
//  val CONTACT_POINTS:String = "192.168.56.101"
  val CONTACT_POINTS:String = "127.0.0.1"
  val PORT = 9042
  val KEYSPACE = "mrjks" // mr.j keyspace

  val COLUMNFAMILY_JUSTIFICATIONS = "justifications"
  val COLUMNFAMILY_RESOURCES = "resources"
  val COLUMNFAMILY_RESULTS = "results" // mr.j keyspace

  val COLUMN_SUB = "sub" // mrjks.justifications.sub

  val COLUMN_PRE = "pre" // mrjks.justifications.pre

  val COLUMN_OBJ = "obj" // mrjks.justifications.obj

  val COLUMN_TRIPLE_TYPE = "tripletype" // mrjks.justifications.tripletype

  val COLUMN_IS_LITERAL = "isliteral" // mrjks.justifications.isliteral

  val COLUMN_INFERRED_STEPS = "inferredsteps" // mrjks.justifications.inferredsteps

  val COLUMN_RULE = "rule" // mrjks.justifications.rule

  val COLUMN_V1 = "v1"
  val COLUMN_V2 = "v2"
  val COLUMN_V3 = "v3"
  val COLUMN_ID = "id" // mrjks.resources.id

  val COLUMN_LABEL = "label" // mrjks.resources.label

  val COLUMN_JUSTIFICATION = "justification" //mrjks.results.justification

  val COLUMN_TRANSITIVE_LEVELS = "transitivelevel" // mrjks.results.step

  val COLUMN_UPDATE_LABEL = "updatelabel" //mrjks.justifications.updatelabel


  var cluster:Cluster=null
  var session:Session=null

  def main(args: Array[String]): Unit = {
    try {
      CassandraDB.connect()
      CassandraDB.createSchema()
      CassandraDB.createTable()
      val results = session.execute("SELECT * FROM "+KEYSPACE+".resources ;")

    } finally CassandraDB.close()
  }


  /**
    * Initiates a connection to the cluster
    * specified by the given contact point. <br>
    * 连接到指定的Cassandra节点。 该节点最好是Seeds server
    *
    */
  def connect(): Unit = {
    cluster = Cluster.builder.addContactPoints(CONTACT_POINTS).withPort(PORT).build
    System.out.printf("Connected to cluster: %s%n", cluster.getMetadata.getClusterName)
    session = cluster.connect()
  }

  /**
    * Creates the schema (keyspace) and tables
    * for this example.
    */
  def createSchema(): Unit = { // 创建Keyspace simplex, 如果之前已经创建了就直接复用
    // 使用SimpleStrategy， 复制因子=1 （数据没有备份，只存放1份）
    try{
      println("create keyspace")
      session.execute("CREATE KEYSPACE IF NOT EXISTS "+KEYSPACE+" WITH replication " + "= {'class':'SimpleStrategy', 'replication_factor':1};")
    }catch{
      case ex:Exception =>
        println("create keyspace failed!")
    }
  }

  def createTable(): Unit = {
    var createSchema:String = ""
    //create 【justification】
    createSchema = getJustificationsSchema
    try{
      println("Create 【justification】")
      session.execute(createSchema)
    }catch{
      case ex:Exception => {
        println("Create 【justification】 failed")
      }
    }

    //create 【resources】
    createSchema = "CREATE TABLE " + KEYSPACE + "." + COLUMNFAMILY_RESOURCES +
      " ( " + COLUMN_ID + " bigint, " +
      COLUMN_LABEL + " text, " +
      "   PRIMARY KEY (" + COLUMN_ID + ") ) "
    try{
      println("Create 【resources】")
      session.execute(createSchema)
    }catch{
      case ex:Exception => {
        println("Create 【resources】 failed")
      }
    }

    //create 【resultrows】
    createSchema = "CREATE TABLE IF NOT EXISTS " +
      CassandraDB.KEYSPACE + "." + "resultrows" + " ( " +
      CassandraDB.COLUMN_IS_LITERAL + " boolean, " + // partition key
      CassandraDB.COLUMN_RULE + " int, " +
      CassandraDB.COLUMN_SUB + " bigint, " + // partition key
      CassandraDB.COLUMN_TRIPLE_TYPE + " int, " +
      CassandraDB.COLUMN_PRE + " bigint, " +
      CassandraDB.COLUMN_OBJ + " bigint, " +
      CassandraDB.COLUMN_V1 + " bigint, " +
      CassandraDB.COLUMN_V2 + " bigint, " +
      CassandraDB.COLUMN_V3 + " bigint, " +
      CassandraDB.COLUMN_INFERRED_STEPS + " int, " + // this is the only field that is not included in the primary key
      CassandraDB.COLUMN_TRANSITIVE_LEVELS + " int, " +
      "   PRIMARY KEY ((" +
      CassandraDB.COLUMN_IS_LITERAL + ", " +
      CassandraDB.COLUMN_RULE + ", " +
      CassandraDB.COLUMN_SUB + "), " +
      CassandraDB.COLUMN_TRIPLE_TYPE + ", " +
      CassandraDB.COLUMN_PRE + ", " +
      CassandraDB.COLUMN_OBJ + ", " +
      CassandraDB.COLUMN_V1 + ", " +
      CassandraDB.COLUMN_V2 + ", " +
      CassandraDB.COLUMN_V3 +
      " ) ) "
    try{
      println("Create 【resultrows】")
      session.execute(createSchema)
    }catch{
      case ex:Exception => {
        println("Create 【resultrows】 failed")
      }
    }

    //create 【results】
    createSchema = "CREATE TABLE " + KEYSPACE + "." + COLUMNFAMILY_RESULTS +
      " ( " + "id" + " int, " +
      COLUMN_JUSTIFICATION + " set<frozen <tuple<bigint, bigint, bigint>>>, " +
      "   PRIMARY KEY (" + "id" + ") ) "
    try{
      println("Create 【results】")
      session.execute(createSchema)
    }catch{
      case ex:Exception => {
        println("Create 【results】 failed")
      }
    }
  }

  /**
    * get Schema of justification
    * @return
    */
  def getJustificationsSchema: String = {
    val schemaString = "CREATE TABLE " + KEYSPACE + "." + COLUMNFAMILY_JUSTIFICATIONS + " ( " +
      COLUMN_SUB + " bigint, " + // partition key
      COLUMN_PRE + " bigint, " +
      COLUMN_OBJ + " bigint, " +
      COLUMN_TRIPLE_TYPE + " int, " +
      COLUMN_RULE + " int, " +
      COLUMN_IS_LITERAL + " boolean, " +
      COLUMN_V1 + " bigint, " +
      COLUMN_V2 + " bigint, " +
      COLUMN_V3 + " bigint, " +
      COLUMN_INFERRED_STEPS + " int, " +
      COLUMN_TRANSITIVE_LEVELS + " int, " +
      COLUMN_UPDATE_LABEL + " int, " +
      "   PRIMARY KEY ("+
      "(" +
      COLUMN_IS_LITERAL + ", " +
      COLUMN_RULE + ", " +
      COLUMN_SUB + "), " +
      COLUMN_TRIPLE_TYPE + ", " + COLUMN_PRE + ", " + COLUMN_OBJ + ", " +
      COLUMN_V1 + ", " + COLUMN_V2 + ", " + COLUMN_V3 +
      " ) ) "
    schemaString
  }
  /**
    * Inserts data into the tables.<br>
    * 插入数据
    */
  def loadData(): Unit = {
    session.execute("INSERT INTO simplex.songs (id, title, album, artist, tags) " + "VALUES (" + "756716f7-2e54-4715-9f00-91dcbea6cf50," + "'La Petite Tonkinoise'," + "'Bye Bye Blackbird'," + "'Joséphine Baker'," + "{'jazz', '2013'})" + ";")
    session.execute("INSERT INTO simplex.playlists (id, song_id, title, album, artist) " + "VALUES (" + "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," + "756716f7-2e54-4715-9f00-91dcbea6cf50," + "'La Petite Tonkinoise'," + "'Bye Bye Blackbird'," + "'Joséphine Baker'" + ");")
  }

  /**
    * Queries and displays data.<br>
    * 查询数据
    */
  def querySchema(): Unit = {
    val results = session.execute("SELECT * FROM simplex.playlists " + "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;")
    System.out.printf("%-30s\t%-20s\t%-20s%n", "title", "album", "artist")
    System.out.println("-------------------------------+-----------------------+--------------------")
    import scala.collection.JavaConversions._
    for (row <- results) {
      System.out.printf("%-30s\t%-20s\t%-20s%n", row.getString("title"), row.getString("album"), row.getString("artist"))
    }
  }

  /**
    * Delete data <br>
    * 删除数据
    */
  private def deleteData() = {
    val results = session.execute("delete from simplex.playlists " + "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;")
    // 删除之后再次进行查询
    querySchema()
  }

  /**
    * Closes the session and the cluster.<br>
    * 最后一定要记得关闭！
    */
  def close(): Unit = {
    session.close()
    cluster.close()
  }

}
