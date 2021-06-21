
import org.apache.spark.{SparkConf, SparkContext}

object Property1{

  def main(args: Array[String]) {

    val conf = new SparkConf().setMaster("local").setAppName("LoopF")

    val spark = new SparkContext(conf)


    //val rdd = spark.textFile("/home/Zhejing/Programs/Benchmarks/src/dataset/salaryEx.csv")
    val rdd = spark.textFile("/home/qzhang/Programs/Benchmarks/src/dataset/loop.txt")
    val data = rdd.map{ s=>
      val a = s.split(",")
      (a(2).toFloat,Integer.parseInt(a(3)),a(4).toFloat,a(6))
    }.map{s=>
        var a = s._1
var i=1-1
while (i<s._2){
i=i+1
        a = a *(1+s._3)
      }
        (a,s._2,s._3,s._4)
    }



    print(data)

    spark.stop()
  }
}
