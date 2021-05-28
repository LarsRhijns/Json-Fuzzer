       .map {
          line =>
            if (line.substring(0, 1).equals("$")) {
              var i = line.substring(1, 6)
              i
            } else {
              line
            }
          }
          .map(p => Integer.parseInt(p))
          .filter(r => r < 300)
          .reduce(_ + _)


filter2 > 1
map3> "1"
map4 > "123"
reduce1> {1,2,3,4}
DAG >reduce1-filter2:filter2-map3:map3-map4
K_BOUND >2
