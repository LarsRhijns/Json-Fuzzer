      .flatMap(line => line.split(","))
       .map(s => (s,1))
       .reduceByKey(2147483600*_+_)

reduceByKey1 > {1,2,3,4}
map2 > ""
flatMap3 >""
DAG >reduceByKey1-map2:map2-flatMap3
