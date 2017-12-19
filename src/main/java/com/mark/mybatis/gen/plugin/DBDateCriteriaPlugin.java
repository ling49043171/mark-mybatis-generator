package com.mark.mybatis.gen.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 用数据库的当前时间，追加比较的Criteria插件。
 * @author 马建华
 *
 */
public class DBDateCriteriaPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {

		InnerClass generatedCriteria = null;
		if("GeneratedCriteria".equals(topLevelClass.getType().getShortName())){
			generatedCriteria = topLevelClass;
		}else{
			List<InnerClass> inners = topLevelClass.getInnerClasses();
			for(InnerClass in : inners){
				if(in.getType().getShortName().startsWith("Generated")){
					generatedCriteria = in;
					break;
				}
			}
		}
		
		if(generatedCriteria == null){
			return true;
		}
		
		for(IntrospectedColumn c : introspectedTable.getAllColumns()){
			
			if("Date".equals(c.getFullyQualifiedJavaType().getShortName())){
				addCurrentMethods(c,generatedCriteria, "Date", getCurrentDateString());
			}else if("Timestamp".equals(c.getFullyQualifiedJavaType().getShortName())){
				addCurrentMethods(c,generatedCriteria, "Timestamp", getCurrentTimestampString());
			}else if("Time".equals(c.getFullyQualifiedJavaType().getShortName())){
				addCurrentMethods(c,generatedCriteria, "Time", getCurrentTimeString());
			}
		}
		
		
		return true;
	}
	
	protected void addCurrentMethods(IntrospectedColumn c, InnerClass criteria, String currentType, String currentSqlType){
		
		//EqualTo
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "EqualToCurrent" + currentType, "=", currentSqlType));
		//NotEqualTo
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "NotEqualToCurrent" + currentType, "<>", currentSqlType));
		//GreterThan
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "GreterThanCurrent" + currentType, ">", currentSqlType));
		//GreaterThanOrEqualTo
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "GreaterThanOrEqualToCurrent" + currentType, ">=", currentSqlType));
		//LessThan
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "LessThanCurrent" + currentType, "<", currentSqlType));
		//LessThanOrEqualTo
		criteria.addMethod(createCurrentMethod(c, "and" + toUpperFirst(c.getJavaProperty()) + "LessThanOrEqualToCurrent" + currentType, "<=", currentSqlType));
		
	}
	
	protected Method createCurrentMethod(IntrospectedColumn c, String methodName, String symbol, String currentSqlType){
		Method m = new Method();
		m.setName(methodName);
		m.setVisibility(JavaVisibility.PUBLIC);
		m.setReturnType(new FullyQualifiedJavaType("Criteria"));
		m.addBodyLine("addCriterion(\"" + c.getActualColumnName() + " " + symbol + " \",\"" + currentSqlType + "\",\"" + c.getJavaProperty() +"\");");
		m.addBodyLine("return (Criteria)this;");
		
		return m;
	}
	
	protected String toUpperFirst(String property){
		String first = String.valueOf(property.charAt(0)).toUpperCase();
		return first + property.substring(1);
	}
	
	protected String getCurrentDateString(){
		return "CURRENT DATE";
	}
	
	protected String getCurrentTimestampString(){
		return "CURRENT TIMESTAMP";
	}
	
	protected String getCurrentTimeString(){
		return "CURRENT TIME";
	}
}
