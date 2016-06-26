//Authors : Yogiraj Awati , Ashish Kalbhor
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import scala.collection.immutable.ListMap
import org.apache.spark.rdd.PairRDDFunctions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.concurrent.TimeUnit

object MissedConnections {
  def checkActualTime(arrTime: String, deptTime: String): Boolean = {
    var arrMins: Int = 0
    var depMins: Int = 0
    try {
      arrMins = java.lang.Integer.parseInt(arrTime.substring(0, 2)) *
        60 +
        java.lang.Integer.parseInt(arrTime.substring(2))
      depMins = java.lang.Integer.parseInt(deptTime.substring(0, 2)) *
        60 +
        java.lang.Integer.parseInt(deptTime.substring(2))
    } catch {
      case e: NumberFormatException => return false
    }
    if ((depMins - arrMins) <= 30) return true
    false
  }
  def checkScheduledTime(arrTime: String, deptTime: String): Boolean = {
    var arrMins: Int = 0
    var depMins: Int = 0
    try {
      arrMins = java.lang.Integer.parseInt(arrTime.substring(0, 2)) *
        60 +
        java.lang.Integer.parseInt(arrTime.substring(2))
      depMins = java.lang.Integer.parseInt(deptTime.substring(0, 2)) *
        60 +
        java.lang.Integer.parseInt(deptTime.substring(2))
    } catch {
      case e: NumberFormatException => return false
    }
    if ((depMins >= arrMins + 30) && (depMins <= 360 + arrMins)) return true
    false
  }
  def main(args: Array[String]) {
    val conf = new SparkConf().
      setAppName("MissedConnections").
      setMaster("local")
    //getting instance of spark context
    val sc = new SparkContext(conf)

    //get all files from all/

    val inputLine = sc.textFile("all/").map { _.replaceAll("\"", "").replaceAll(", ", "#") }

    //Split inputLine on "," to get data from excel sheet

    val formattedLine = inputLine.map { _.split(",") }

    //ignoring headers by checking first coloumn == "YEAR". Skip headers
    val flightInfo = formattedLine.filter { _(0) != "YEAR" }

    //key: carrier name,Year,Origin/Destination Id,date 
    //Value: arrv time/dept time + sched arrv time/sched dept time+ date+ cancellation
    //originDetails contains data about the origin of the flight 
    val originDetails = flightInfo.map(
      //Key : carrier name,Year,Origin Id,date
      orgData => (orgData(6) + "," + orgData(0) + "," + orgData(11) + "," + orgData(5),

        //Value : arrv time + sched arrv time+ date+ cancellation
        Array(orgData(41), orgData(40), orgData(5), orgData(47))))

    //destinationDetails contains data about the destination of the flight 
    val destinationDetails = flightInfo.map(
      //Key : carrier name,Year,Destination Id,date
      destData => (destData(6) + "," + destData(0) + "," + destData(20) + "," + destData(5),

        //Value : dept time + sched dept time+ date+ cancellation
        Array(destData(30), destData(29), destData(5), destData(47))))

    //Join two RDDs
    val data = originDetails.join(destinationDetails)

    val validRecords = data.map(
      r => {
        val (key, value) = r;
        val (origin, destination) = value;
        //Retreive data of origin : 

        val arrtime = (origin(0))
        val crsArrTime = (origin(1))
        val orgFlightdate = (origin(2))
        val cancellation = (origin(3))

        //Retreive data of destination 
        val deptime = (destination(0))
        val crsDepTime = (destination(1))
        val destFlightdate = (destination(2))
        val destCancellation = (destination(3))

        if (!arrtime.isEmpty && !deptime.isEmpty && !orgFlightdate.isEmpty && !destFlightdate.isEmpty) {

          if (checkScheduledTime(crsArrTime, crsDepTime)) {
            if (checkActualTime(arrtime, deptime)) {
              (key, (0, 1, 1))
            } else {
              (key, (1, 0, 1))
            }
          } else {
            (key, (0, 0, 1))
          }

        } else {
            (key, (0, 0, 1))          
        }
      })

    val reduceText = validRecords.reduceByKey((p1, p2) => (p1._1 + p2._1, p1._2 + p2._2, p1._3 + p2._3))

    //		 reduceText.foreach(println)

    val textCollect = reduceText.collect()

    //		 textCollect.foreach(println)

    val output = textCollect.map(value => {
      val (k, v) = value;
      val (p1, p2) = v;

      k + "," + p1.toString + "," + p2.toString

    })

    var result = sc.parallelize(output)

    result.saveAsTextFile("out")

    // Shut down Spark, avoid errors.
    sc.stop()

  }
}
