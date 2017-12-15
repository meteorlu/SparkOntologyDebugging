package utils

/**
  * Created by user on 2017/12/12.
  */
object SparkConfig {
  val master_local = "local[1]"
  val master_standalone = "spark://192.168.168.150:7077"
  val spark_driver_cores = "spark.driver.cores"
  val spark_driver_cores_num = "2"
  val spark_driver_memory = "spark.driver.memory"
  val spark_driver_memory_num = "2"
}
