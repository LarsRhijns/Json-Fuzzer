      // Trips
      val trips = sc
      .textFile(
        "/home/qzhang/Programs/eclipse/Test-Minimization-in-Big-Data-JPF-integrated/benchmarks/src/datasets/trips/*")
      .map { line1 =>
        val cols = line1.split(",")
        (cols(1), Integer.parseInt(cols(3)) / Integer.parseInt(cols(4)))
      }
    val locations = sc
      .textFile(
        "/home/qzhang/Programs/eclipse/Test-Minimization-in-Big-Data-JPF-integrated/workspace/up_jpf/benchmarks/src/datasets/zipcode/*")
      .map { line2 =>
        val cols = line2.split(",")
        (cols(0), cols(1))
      }
     .filter{v =>
       val t1 = v._1
       val t2 = v._2
       t2.equals("Palms")}
   val joined = trips.join(locations)
   joined
     .map { s =>
        // Checking if speed is < 25mi/hr
        if (log10(s._2._1) > 40) {
          ("car", 1)
        } else if (log10(s._2._1) > 15) {
          ("public", 1)
        } else {
          ("walk", 1)
        }
      }
      .reduceByKey(_ + _)



map1>","
map3>","
filter2>"",""
map5>"1" , 2, "1"
reduceByKey4> {1,2,3,4}
K_BOUND>2
DAG >reduceByKey4-map5:map5-join:join-map1,filter2:filter2-map3
