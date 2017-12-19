package com.mark.mybatis.gen.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;
import java.util.Properties;

/**
 * Model类里的Json序列化标识。
 * @author 马建华
 *
 */
public class ModelFieldAddJsonFilterAnotationPlugin extends PluginAdapter {


	@Override
	public boolean validate(List<String> warnings) {
//		fieldFilters = properties.getProperty("fieldFilters");
//		String strFieldFiltersByTable = properties.getProperty("fieldFiltersByTable");
//		if (strFieldFiltersByTable != null && strFieldFiltersByTable.length() != 0) {
//			fieldFiltersByTable = JSONObject.parseObject(strFieldFiltersByTable);
//		}
		return true;
	}

	@Override
	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable, ModelClassType modelClassType) {

		Properties properties = introspectedColumn.getProperties();
		String jsonSerialize = properties.getProperty("serialize");
		if (jsonSerialize != null) {
            field.addAnnotation("@"+ new FullyQualifiedJavaType("com.alibaba.fastjson.annotation.JSONField") +"("+jsonSerialize+")");
        }

//		List<String> lst = Arrays.asList(filters);
//		if (lst.contains(introspectedColumn.getActualColumnName())) {
//			field.addAnnotation("@"+ new FullyQualifiedJavaType("com.alibaba.fastjson.annotation.JSONField") +"(serialize=false,deserialize=false)");
//		} else if (fieldFiltersByTable != null) {
//			String tableName = introspectedTable.getFullyQualifiedTable().toString();
//			System.out.println(tableName);
//			JSONArray tableFields = fieldFiltersByTable.getJSONArray(tableName);
//			if (tableFields != null) {
//				for (int i = 0; i < tableFields.size(); i++) {
//					if (introspectedColumn.getActualColumnName().equals(tableFields.getString(i))) {
//						field.addAnnotation("@"+ new FullyQualifiedJavaType("com.alibaba.fastjson.annotation.JSONField") +"(serialize=false,deserialize=false)");
//					}
//				}
//			}
//		}
		return true;
	}
}
