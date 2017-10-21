package meta.z.musicut.util;

public class MathUtils
{ 
	public static double constrain(double cur,double min,double max){
		return Math.min(Math.max(min,cur),max);
	}
}
