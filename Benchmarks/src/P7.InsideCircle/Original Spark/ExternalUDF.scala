

import org.apache.spark.{SparkConf, SparkContext}

object ExternalUDF{

  def inside(x:Int, y:Int, z:Int): Boolean ={
    x*x+y*y<z*z
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("LoopF")

    val sc = new SparkContext(conf)


    val text = sc.textFile("/home/qzhang/Programs/BigFuzz-TestPrograms/src/dataset/income")
    val data = text.map {
      s =>
        val cols = s.split(",")
        (Integer.parseInt(cols(0)), Integer.parseInt(cols(1)), Integer.parseInt(cols(2)))
    }.filter(s=>inside(s._1,s._2,s._3))





    print(data.count())

    sc.stop()
  }
}