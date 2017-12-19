package com.mark.mybatis.gen.plugin;

/**
 * MySql的DBCriteriaPlugin
 * 
 * @author 马建华
 *
 */
public class MySqlDBDateCriteriaPlugin extends DBDateCriteriaPlugin {

	@Override
	protected String getCurrentDateString() {
		return "CURRENT_DATE";
	}

	@Override
	protected String getCurrentTimestampString() {
		return "CURRENT_TIMESTAMP";
	}

	@Override
	protected String getCurrentTimeString() {
		return "CURRENT_TIME";
	}
	
}
