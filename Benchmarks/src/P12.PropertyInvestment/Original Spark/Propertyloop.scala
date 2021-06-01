.map{ s=>
  val a = s.split(",")
  (a(2).toFloat,Integer.parseInt(a(3)),a(4).toFloat,a(6))
}.map{s=>
  var a = s._1
  for (i<-1 to s._2) {
    a = a *(1+s._3)
  }
  (a,s._2,s._3,s._4)
}


filter1 > "",""
map2 > ","
DAG >filter1-map2
K_BOUND>2
