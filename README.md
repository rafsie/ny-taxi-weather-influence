# ny-taxi-weather-influence

This is quite a simple but time-consuming (especially assuming operation on an old Dell i5 notebook:) analysis on the effect of weather on the number of NYC taxi rides on particular days of the week.

The [dataset](https://academictorrents.com/details/4f465810b86c6b793d1c7556fe3936441081992e) contains 1,382,375,998 records of New York taxi routes from 2009-2016, with a volume of approx. 35 GB in compressed parquet format. This is equivalent to approximately 400 GB of uncompressed data in the form of text (_csv_) files.

The calculations were made using the Apache Spark framework and the Apache Zeppelin notebook. As it is currently not possible to display the rendered content of Zeppelin notebooks on Github, screenshot containing target graph, as well as the complete project in the IntelliJ environment are presented.

A cluster consisting of a master-node and three worker-nodes running in a Docker container was used. The popular [Spark 2.4.5 image for Hadoop 2.7+ with OpenJDK 8](https://github.com/big-data-europe/docker-spark) was used as the source, however, some changes were made to the docker-compose.yml file:

* history-server was removed, because the ability to view tasks will be provided by the Apache Zeppelin notebook used as an environment for analysis and graphical presentation of results, if necessary

* an additional spark-worker-3 was added in its place, as well as the necessary environmental parameters of the cluster, taking into account hardware resources and data volumes.

After starting the cluster with the _docker-compose up_ command, spark-master's IP address was read:

&emsp; _$ sudo docker inspect -f ‚{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}’ spark-master_

and the value was entered into the settings of the locally running Zeppelin's built-in Spark interpreter: 

&emsp; _spark.master &emsp; spark://172.18.0.2:7077_

The _csv_ file containing historical meteorological data used also here was collected form [Wunderground](https://www.wunderground.com/history/) and saved thanks to a slightly modified script available at Bojan Stavrikj Github [homepage](https://bojanstavrikj.github.io/content/page1/wunderground_scraper).

All units for temperature, wind speed and precipitation have been converted on the fly from imperial to metric using simple functions written in Scala.

For the purposes of the chart, a simple assumption was made that the yellow line in the chart (_AvgTripsNormal_) means the number of trips in optimal weather conditions classified as: average temperature (_AvgTempC_) between 10 and 25 Celsius degree, average wind speed (_AvgWindKmh_) below 20 kilometer per hour and precipitation (PrecipitationMMm2) equal to zero.

In turn, the blue line (_AvgTripsCold_) indicates the number of trips with an average temperature below 10 degrees Celsius, wind speed above 20 km/h, and precipitation greater than 0. Modification of these simple criteria is easy thanks to the possibility of creating a new table view and slightly modifying the target SQL query.

![weather-trips.png](src%2Fmain%2Fresources%2Fimg%2Fweather-trips.png)

The chart was created with the ultimate-line-chart plugin from [amCharts](https://www.amcharts.com/).
