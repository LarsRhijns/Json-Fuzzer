package edu.ucla.cs.bigfuzz.customarray.inapplicable.OneDFOperator;

import edu.berkeley.cs.jqf.instrument.tracing.TraceLogger;
import edu.ucla.cs.bigfuzz.customarray.CustomArray;
import edu.ucla.cs.bigfuzz.dataflow.*;
import janala.logger.inst.METHOD_BEGIN;
import janala.logger.inst.MemberRef;

import java.util.*;public class OneDFOperatorCustomArray {

    private ArrayList<Object> list;

    public void OneDFOperatorCustomArray() {

    }
 public static ArrayList< String> Map1(ArrayList<String> result){
        int callersLineNumber = Thread.currentThread().getStackTrace()[1].getLineNumber();

        int iid = CustomArray.class.hashCode(); // this should be a random value associated with a program location
        MemberRef method = new METHOD_BEGIN(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), "()V"); // containing method

        ArrayList< String> ans = new ArrayList<>();
        TraceLogger.get().emit(new MapEvent(iid, method, callersLineNumber));
for (String results: result){ans.add( map1.apply( results));}
return ans;
}
}