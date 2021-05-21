package edu.ucla.cs.bigfuzz.customarray.inapplicable.ExternalUDF;

public class map2 {
   public static void main(String[] args) { 
       apply(",");
   }
  static final map2 apply(String s){
  String a[]=s.split(",");
  return new map2(f.apply(a[0]),(Integer.parseInt(a[1])));
}
String sa,sb;

int ia,ib;
public int _1(){
	return ia;
}
public int _2(){
	return ib;
}
public map2(int k, int v){
        ia = k;
        ib = v;
}
public map2(String k, int v){
        sa = k;
        ib = v;
}
public map2(int k, String v){
        ia = k;
        sb = v;
}
public map2(String k, String v){
        sa = k;
        sb = v;
}}