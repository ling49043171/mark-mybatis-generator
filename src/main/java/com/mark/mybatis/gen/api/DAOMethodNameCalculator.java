package com.mark.mybatis.gen.api;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.internal.DefaultDAOMethodNameCalculator;

/**
 * 
 * @author Mark
 *
 */
public class DAOMethodNameCalculator extends
        DefaultDAOMethodNameCalculator {

	@Override
	public String getDeleteByExampleMethodName(
			IntrospectedTable introspectedTable) {
		return super.getDeleteByExampleMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getSelectByExampleWithoutBLOBsMethodName(
			IntrospectedTable introspectedTable) {
		return super.getSelectByExampleWithoutBLOBsMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getSelectByExampleWithBLOBsMethodName(
			IntrospectedTable introspectedTable) {
		return super.getSelectByExampleWithBLOBsMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getCountByExampleMethodName(
			IntrospectedTable introspectedTable) {
		return super.getCountByExampleMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getUpdateByExampleSelectiveMethodName(
			IntrospectedTable introspectedTable) {
		return super.getUpdateByExampleSelectiveMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getUpdateByExampleWithBLOBsMethodName(
			IntrospectedTable introspectedTable) {
		return super.getUpdateByExampleWithBLOBsMethodName(introspectedTable).replace("Example", "Criteria");
	}

	@Override
	public String getUpdateByExampleWithoutBLOBsMethodName(
			IntrospectedTable introspectedTable) {
		return super.getUpdateByExampleWithoutBLOBsMethodName(introspectedTable).replace("Example", "Criteria");
	}

	
}
