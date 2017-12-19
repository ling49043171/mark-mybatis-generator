package com.mark.mybatis.gen.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 追加乐观锁检查方法的插件。
 *
 * @author 马建华
 */
public class OptimisticLockMethodPlugin extends PluginAdapter {

    private String versionColumnName = "version";

    private String optimisticLockException;

    @Override
    public boolean validate(List<String> warnings) {
        // 不等于1的场合不生成
        optimisticLockException = properties.getProperty("optimisticLockException");
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(
            Method method, Interface interfaze,
            IntrospectedTable introspectedTable) {
        if (ignore(introspectedTable)) {
            return true;
        }

        Method m = new Method();
        m.setName("updateByPrimaryKeySelectiveWithVersion");
        copyMethod(m, method, null);
        m.addParameter(new Parameter(new FullyQualifiedJavaType(
                "java.lang.Integer"), "version", "@Param(\"version\")"));
        m.addException(new FullyQualifiedJavaType(optimisticLockException));

        interfaze.addMethod(m);
        interfaze.addImportedType(new FullyQualifiedJavaType(optimisticLockException));
        return true;
    }
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (ignore(introspectedTable)) {
            return true;
        }
        return generateOptimisticUpdateXml(element, introspectedTable, "update",
                introspectedTable.getUpdateByPrimaryKeySelectiveStatementId());
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method,
                                                           Interface interfaze, IntrospectedTable introspectedTable) {
        if (ignore(introspectedTable)) {
            return true;
        }
        Method m = new Method();
        m.setName("deleteByPrimaryKeyWithVersion");
        copyMethod(m, method, introspectedTable.getPrimaryKeyColumns());
        m.addParameter(new Parameter(new FullyQualifiedJavaType("java.lang.Integer"), "version","@Param(\"version\")"));
        m.addException(new FullyQualifiedJavaType(optimisticLockException));

        interfaze.addMethod(m);
        interfaze.addImportedType(new FullyQualifiedJavaType(optimisticLockException));
        return true;
    }

    private boolean ignore(IntrospectedTable introspectedTable) {
        IntrospectedColumn version = introspectedTable.getColumn(versionColumnName);
        return version == null;
    }

    private String convert2JavaType(String field) {
        int index = field.indexOf("_");
        if (index >= 0) {
            if (index >= field.length()-1) {
                return field.substring(0, index);
            } else {
                return field.substring(0, index) + field.substring(index+1, index+2).toUpperCase() + convert2JavaType(field.substring(index+2));
            }
        }
        return field;
    };
    private void copyMethod(Method m, Method method, List<IntrospectedColumn> keyColums) {
        m.setReturnType(method.getReturnType());
        m.setVisibility(method.getVisibility());
        m.setFinal(method.isFinal());
        m.setStatic(method.isStatic());
        m.setConstructor(method.isConstructor());

        for (Parameter p : method.getParameters()) {
            if ("record".equals(p.getName()) && p.getAnnotations().isEmpty()) {
                p.addAnnotation("@Param(\"record\")");
            } else if (keyColums != null) {
                for (int i = 0; i < keyColums.size(); i++) {
                    IntrospectedColumn keyColum = keyColums.get(i);
                    String dbField = convert2JavaType(keyColum.getActualColumnName());
                    if (dbField.equals(p.getName())) {
                        p.addAnnotation(String.format("@Param(\"%s\")", p.getName()));
                        break;
                    }
                }
            }
            m.addParameter(p);
        }
        for (FullyQualifiedJavaType jt : method.getExceptions()) {
            m.addException(jt);
        }
        boolean found = false;
        for (int i = 0; i < method.getAnnotations().size(); i++) {
            String s = method.getAnnotations().get(i);
            if (i == 0 && s.startsWith("@Delete")) {
                found = true;
            }
            if (found && i == method.getAnnotations().size() - 1) {
                m.addAnnotation(StringUtils.trimTrailingWhitespace(s.substring(0, s.length()-2)) +",");
                m.addAnnotation("\"and version= #{version,jdbcType=INTEGER}\"})");
            } else {
                m.addAnnotation(s);
            }
        }
        for (String s : method.getJavaDocLines()) {
            m.addJavaDocLine(s);
        }
    }

    private boolean generateOptimisticUpdateXml(XmlElement element, IntrospectedTable introspectedTable, String idType, String id) {
        if (ignore(introspectedTable)) {
            return true;
        }
        XmlElement answer = new XmlElement(idType);
        answer.addAttribute(new Attribute("id", id + "WithVersion"));
        answer.addAttribute(new Attribute("parameterType", "map"));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append(idType + " ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set");
        answer.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable
                .getAllColumns())) {
            sb.setLength(0);
            if ("version".equals(introspectedColumn.getActualColumnName())) {
                TextElement version = new TextElement("version = version+1,");
                dynamicElement.addElement(version);
                continue;
            }

            XmlElement isNotNullElement = new XmlElement("if");

            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);
            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("  where ");
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));
            answer.addElement(new TextElement(sb.toString()));
        }
        answer.addElement(new TextElement(" and version = #{version,jdbcType=INTEGER}"));

        String sql = answer.getFormattedContent(4);
        TextElement txt = new TextElement(sql.substring(0, sql.length() - (idType.length() + 3)));
        element.addElement(new TextElement("</" + idType + ">"));
        element.addElement(txt);
        return true;
    }
}
