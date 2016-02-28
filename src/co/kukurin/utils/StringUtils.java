package co.kukurin.utils;

public class StringUtils {
	
	private StringUtils() {}
	
	public static boolean containsEmptyString(String ... str) {
		if(str == null || str.length == 0)
			return true;
		
		for(String s : str)
			if(s == null || s.isEmpty())
				return true;
		
		return false;
	}
}
