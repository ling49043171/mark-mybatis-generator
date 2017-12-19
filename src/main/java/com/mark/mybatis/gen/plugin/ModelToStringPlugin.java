package com.mark.mybatis.gen.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * Model类里的toString方法的覆盖插件。
 * @author 马建华
 *
 */
public class ModelToStringPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		createToString(topLevelClass);
		for(InnerClass ic : topLevelClass.getInnerClasses()){
			createToString(ic);
		}
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		createToString(topLevelClass);
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		createToString(topLevelClass);
		return true;
	}
	
	private void createToString(InnerClass cls){
		Method m = new Method();
		m.setReturnType(FullyQualifiedJavaType.getStringInstance());
		m.setVisibility(JavaVisibility.PUBLIC);
		m.setName("toString");
		m.addAnnotation("@Override");
		
		StringBuilder sb = new StringBuilder();
		sb.append("return \"");
		sb.append(cls.getType().getShortName()).append(" [");
		
		int count = 0;
		for(Field f : cls.getFields()){
			if(!f.isStatic()){
				if(count > 0){
					sb.append(",");
				}
				sb.append(f.getName()).append("=\" + ").append(f.getName()).append(" + \"");
				count++;
			}
		}
		if(cls.getSuperClass() == null){
			sb.append("]\";");
		}else{
			sb.append("->\" + super.toString() + \"]\";");
		}
		
		m.addBodyLine(sb.toString());
		
		cls.addMethod(m);
	}
}
