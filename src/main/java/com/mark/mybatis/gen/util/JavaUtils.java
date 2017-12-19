package com.mark.mybatis.gen.util;

public class JavaUtils {
	private JavaUtils() {}
	
	public static String toVariableName(String className){
		if(className == null || className.equals("")){
			throw new IllegalArgumentException("argument is empty");
		}
		String ret = className.substring(0,1).toLowerCase();
		if(className.length() > 1){
			ret = ret + className.substring(1);
		}
		return ret;
	}
}
