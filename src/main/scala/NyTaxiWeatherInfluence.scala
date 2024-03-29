import org.apache.spark.sql.{ColumnName, SparkSession}
import org.apache.spark.sql.functions._

object NyTaxiWeatherInfluence extends App {

  val spark = SparkSession.builder()
    .config("spark.master", "local[*]")
    .appName("NyTaxiWeatherInfluence")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  import spark.implicits._

  val nyTaxiDF = spark.read.format("parquet")
    .option("header", "true")
    .option("mode", "FAILFAST")
    .option("inferSchema", "true")
    .load("/home/rs/Desktop/ntfs/nytaxi/part-*.snappy.parquet")
//  .load("/home/rs/Desktop/ntfs/nytaxi/part-r-00000-ec9cbb65-519d-4bdb-a918-72e2364c144c.snappy.parquet")

  val nyWeatherDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("/home/rs/Desktop/ntfs/nytaxi/nyc_weather.csv")

  def fahrenheitToCelsius(f: ColumnName) = round((f - 32) * 5 / 9, 1)
  def milesToKmPerHour(m: ColumnName) = round(m * 1.609344, 1)
  def inchesToMm(i: ColumnName) = round(i * 25.4, 1)

  val weatherDF = nyTaxiDF
    .select("pickup_datetime")
    .withColumn("pickup_date", to_date(col("pickup_datetime"), "yyyy-MM-dd"))
    .withColumn("DOW", date_format(col("pickup_date"), "E"))
    .withColumn("WeekDayNo", dayofweek(col("pickup_date")))
    .join(nyWeatherDF, col("pickup_date") <=> col("Date").cast("date"))
    .groupBy($"pickup_date".as("Date"),
      $"WeekDayNo".as("WeekDayNo"),
      $"DOW".as("DOW"),
      fahrenheitToCelsius($"Temp_avg").as("AvgTempC"),
      milesToKmPerHour($"Wind_avg").as("AvgWindKmh"),
      inchesToMm($"Precipitation").as("PrecipitationMMm2"))
    .agg(count("*").as("TripsNo"))
    .orderBy(col("Date").asc_nulls_last)

  weatherDF.createOrReplaceTempView("weathertbl")
  weatherDF.show(25)

  spark.sql("CREATE OR REPLACE TEMPORARY VIEW normalweathertbl AS " +
    "SELECT WeekDayNo, DOW, round(avg(TripsNo), 2) AS AvgTripsNormal FROM weathertbl " +
    "WHERE AvgTempC BETWEEN 10 AND 25 AND AvgWindKmh < 20 AND PrecipitationMMm2 = 0 " +
    "GROUP BY DOW, WeekDayNo " +
    "ORDER BY WeekDayNo ASC")

  val result = spark.sql("SELECT wt.WeekDayNo, wt.DOW, nwt.AvgTripsNormal, round(avg(TripsNo), 2) AS AvgTripsCold FROM weathertbl wt " +
    "INNER JOIN normalweathertbl nwt " +
    "ON wt.WeekDayNo = nwt.WeekDayNo " +
    "WHERE AvgTempC < 10 AND AvgWindKmh > 20 AND PrecipitationMMm2 > 0 " +
    "GROUP BY wt.DOW, wt.WeekDayNo, nwt.AvgTripsNormal " +
    "ORDER BY WeekDayNo ASC")

  result.show(25)
}
