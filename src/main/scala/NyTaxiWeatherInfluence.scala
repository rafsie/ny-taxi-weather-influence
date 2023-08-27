import org.apache.spark.sql.{ColumnName, SparkSession}
import org.apache.spark.sql.functions._

object NyTaxiWeatherInfluence extends App {

  val spark = SparkSession.builder()
    .config("spark.master", "local[*]")
    .appName("NyTaxi")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val nyTaxiDF = spark.read.format("parquet")
    .option("header", "true")
    .option("mode", "FAILFAST")
    .option("inferSchema", "true")
//    .load("/home/rs/Desktop/ntfs/nytaxi/part-*.snappy.parquet")
  .load("/home/rs/Desktop/ntfs/nytaxi/part-r-00000-ec9cbb65-519d-4bdb-a918-72e2364c144c.snappy.parquet")

  val nyZonesDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("/home/rs/Desktop/ntfs/nytaxi/taxi_zone_lookup.csv")

  val nyWeatherDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("/home/rs/Desktop/ntfs/nytaxi/nyc_weather.csv")

}
