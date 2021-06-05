    val text = sc.textFile("/home/qzhang/Programs/BigFuzz-TestPrograms/src/dataset/income")
    val data = text.map {
      s =>
        val cols = s.split(",")
        (cols(0), Integer.parseInt(cols(1)), Integer.parseInt(cols(2)))
    }.filter( s => s._1.equals("90024"))


    val pair = data.map {
      s =>
      // Checking if age is within certain range
      if (s._2 >= 40 & s._2 <= 65) {
        ("40-65", s._3)
      } else if (s._2 >= 20 & s._2 < 40) {
        ("20-39", s._3)
      } else if (s._2 < 20){
        ("0-19", s._3)
      } else {
        (">65", s._3)
      }
    }//.reduceByKey(_+_).foreach(println)

    val sum = pair.mapValues( x => (x, 1))
      .reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
      .mapValues(x => (x._2, x._1.toDouble / x._2.toDouble))
      .foreach(println)
      

mapValues1>1,1
reduceByKey2>{1,2,3,4},{1,2,3,4}
mapValues3>1
map4>"",1,1
filter5>"",1,1
map6>","
DAG:mapValues1-reduceByKey2:reduceByKey2-mapValues3;mapValues3-map4:map4-filter5:filter5-map6
K_BOUND>2
