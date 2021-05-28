      .flatMap(line => line.split(","))
       .map(s => (s,1))
       .reduceByKey(_+_)
      .filter{ v => 
         val v1 = log10(v._2)
         v1 > 1
      }

reduceByKey1 > {1,2,3,4}
map2 > ""
flatMap3 >""
DAG >reduceByKey1-map2:map2-flatMap3
