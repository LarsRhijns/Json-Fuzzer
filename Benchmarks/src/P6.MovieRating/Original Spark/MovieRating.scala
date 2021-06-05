           .map { line =>
            val arr = line.split(":")
            val movie_str = arr(0)
            val ratings = arr(1).split(",")(0).split("_")(1)
            (movie_str, ratings.substring(0, 1))
          }
          .map { a =>
            val str = a._1
            (a._1, Integer.parseInt(a._2))
          }
          .filter { v =>
            val tw = v._1
            //val t2 = b._2
            v._2 > 4
          }.reduceByKey(_ + _)



map4>": ,"
map3>"", "1"
filter2>"",1
reduceByKey1> {1,2,3,4}
K_BOUND>2
DAG >reduceByKey1-filter2:filter2-map3:map3-map4
