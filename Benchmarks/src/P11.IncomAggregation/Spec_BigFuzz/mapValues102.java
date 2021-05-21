package P11.IncomAggregation.Spec_BigFuzz;

import scala.Tuple2;

public class mapValues102{

            public static final Tuple2 apply(Tuple2 x)
            {
                return new Tuple2(x._2(), (double)x._1() / (double)x._2());
            }


            public static final long serialVersionUID = 0L;

        }
