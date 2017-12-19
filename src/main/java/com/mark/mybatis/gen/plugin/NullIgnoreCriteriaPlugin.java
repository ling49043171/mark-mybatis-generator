package com.mark.mybatis.gen.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 做成Criteria条件时，为了去掉NULL检查，无视NULL值得插件。
 * @author 马建华
 *
 */
public class NullIgnoreCriteriaPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		List<InnerClass> inners = topLevelClass.getInnerClasses();
		for(InnerClass in : inners){
			if(in.getType().getShortName().startsWith("Generated")
					|| in.getType().getShortName().startsWith("Criteria")){
				for(Method m : in.getMethods()){
					if(m.getName().startsWith("and") && m.getParameters().size() > 0){
						m.addBodyLine(0, String.format("if(%s)return (Criteria)this;",getIfExpressionStringBy(m.getParameters())));
					}
				}
			}
			if (in.getType().getShortName().startsWith("Criteria")) {
				Method method = new Method();
				method.setName("setRowNum");
				FullyQualifiedJavaType fqjt = FullyQualifiedJavaType.getCriteriaInstance();
				method.setReturnType(fqjt);
				method.setVisibility(JavaVisibility.PUBLIC);
				Parameter p = new Parameter(FullyQualifiedJavaType.getStringInstance(), "rowNum");
				method.addParameter(p);
				method.addBodyLine(String.format("if(%s)return (Criteria)this;",getIfExpressionStringBy(method.getParameters())));
				method.addBodyLine(setRowNum[0]);
				method.addBodyLine(setRowNum[1]);
				method.addBodyLine(setRowNum[2]);
				method.addBodyLine(setRowNum[3]);
				method.addBodyLine(setRowNum[4]);
				method.addBodyLine("return this;");
				in.addMethod(method);
			}
		}
		return true;
	}
	private static String setRowNum[] = {"try {",
			"addCriterion(\"ROWNUM <=\" + (Integer.parseInt(rowNum) + 1));",
			"} catch (NumberFormatException nfe) {",
			"throw new RuntimeException(nfe);",
			"}"};
	
	
	protected String getIfExpressionStringBy(List<Parameter> params){
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(Parameter p : params){
			if(count > 0){
				sb.append(" || ");
			}
			
			sb.append(p.getName()).append(" == null");
			
			count++;
		}
		return sb.toString();
	}
}
