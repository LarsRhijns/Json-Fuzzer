      .flatMap { line =>
        val arr = line.split(",")
        arr
      }
      .map { l=>
        val a = l.split(":")
        (a(0), Integer.parseInt(a(1)))
      }
      .map { a =>
        if (a._2 > 40)
          (a._1.concat(" Pass"), 1)
        else
          (a._1.concat(" Fail"), 1)
      }
      .reduceByKey(_+_)
      .filter { v =>
        val tw = v._1
        v._2 > 40
      }



filter1 > "",1
map3> "",1
map4 > "CS:123"
reduceByKey2 > {1,2,3,4}
flatMap5 > "a,a"
DAG >filter1-reduceByKey2:reduceByKey2-map3:map3-map4:map4-flatMap5
K_BOUND>2
