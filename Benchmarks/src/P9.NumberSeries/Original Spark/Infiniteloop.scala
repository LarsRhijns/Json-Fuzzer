      .map{ s =>
      val cols = s.split(",")
      Integer.parseInt(cols(1))
    }.map{ l=>
      var dis = 1
      var tmp = l

      if(l <= 0){
        dis = 0
      }else {
        while (tmp != 1) {
          if (tmp % 2 == 0) {
            tmp = tmp / 2
          } else {
            tmp = 3 * tmp + 1
          }
          dis = dis + 1
        }
      }
      (l, dis)
    }//.foreach(println)
      .filter(m => m._2.equals(25))



filter1 > 1,1
map2 > 1
map3 > "1,"
DAG >filter1-map2:map2-map3
K_BOUND>2
