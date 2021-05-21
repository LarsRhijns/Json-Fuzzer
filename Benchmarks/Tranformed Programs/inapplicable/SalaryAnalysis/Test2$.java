// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Test2.scala

package examples;

import java.io.PrintStream;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.rdd.RDD;
import scala.*;
import scala.reflect.ClassTag$;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxesRunTime;

public final class Test2$
{

    public String f(String s)
    {
        return s;
    }

    public void main(String args[])
    {
        SparkConf conf = (new SparkConf()).setAppName("Scala Toy Example 1: Add Integers").setMaster("local[4]");
        SparkContext sc = new SparkContext(conf);
        sc.textFile("logfile", sc.textFile$default$2());
        RDD text = sc.textFile("/home/qzhang/Programs/Benchmarks/src/dataset/salary.csv", sc.textFile$default$2());
        RDD data = text.map(new Serializable() {

            public final Tuple3 apply(String s)
            {
                String cols[] = s.split(",");
                return new Tuple3(cols[0], BoxesRunTime.boxToInteger(Integer.parseInt(cols[1])), BoxesRunTime.boxToInteger(Integer.parseInt(cols[2])));
            }

            public final volatile Object apply(Object v1)
            {
                return apply((String)v1);
            }

            public static final long serialVersionUID = 0L;

        }, ClassTag$.MODULE$.apply(scala/Tuple3)).filter(new Serializable() {

            public final boolean apply(Tuple3 s)
            {
                return ((String)s._1()).equals("90024");
            }

            public final volatile Object apply(Object v1)
            {
                return BoxesRunTime.boxToBoolean(apply((Tuple3)v1));
            }

            public static final long serialVersionUID = 0L;

        });
        int pair = ((Tuple2[])data.map(new Serializable() {

            public final Tuple2 apply(Tuple3 s)
            {
                return (BoxesRunTime.unboxToInt(s._2()) >= 40) & (BoxesRunTime.unboxToInt(s._2()) <= 65) ? new Tuple2("40-65", s._3()) : (BoxesRunTime.unboxToInt(s._2()) >= 20) & (BoxesRunTime.unboxToInt(s._2()) < 40) ? new Tuple2("20-39", s._3()) : BoxesRunTime.unboxToInt(s._2()) >= 20 ? new Tuple2(">65", s._3()) : new Tuple2("0-19", s._3());
            }

            public final volatile Object apply(Object v1)
            {
                return apply((Tuple3)v1);
            }

            public static final long serialVersionUID = 0L;

        }, ClassTag$.MODULE$.apply(scala/Tuple2)).collect()).length;
        if(pair > 7)
        {
            System.out.println(pair);
            Predef$.MODULE$.assert(pair != 8);
        }
    }

    private Test2$()
    {
    }

    public static final Test2$ MODULE$ = this;

    static
    {
        new Test2$();
    }
}