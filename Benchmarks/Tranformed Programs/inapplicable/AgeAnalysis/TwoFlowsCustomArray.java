package edu.ucla.cs.bigfuzz.customarray.inapplicable.TwoFlows;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.dataflow.*;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;
import scala.Tuple3;

import java.util.*;public class TwoFlowsCustomArray {

    private ArrayList<Object> list;

    public void TwoFlowsCustomArray() {

    }
 public static ArrayList< Tuple3> Map1(ArrayList<String> result){
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();

        int iid = CustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method

        ArrayList<Tuple3> ans = new ArrayList<>();
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));
for (String results: result){ans.add( map3.apply( results));}
return ans;
}
 public static ArrayList< Tuple3> Filter1(ArrayList< Tuple3> result){
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();

        int iid = CustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method

        ArrayList< Tuple3> ans = new ArrayList<>();
for ( Tuple3 results: result){if (filter1.apply(results)) ans.add(results );}
     int arm = 0;
     if (!ans.isEmpty()) arm=1;
     TraceLogger.get().emit(new FilterEvent(iid, method, callersLineNumber,arm));
return ans;
}
 public static ArrayList< map2> Map2(ArrayList< Tuple3> result){
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();

        int iid = CustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method

        ArrayList< map2> ans = new ArrayList<>();
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));
for ( Tuple3 results: result){ans.add( map2.apply( results));}
return ans;
}
}