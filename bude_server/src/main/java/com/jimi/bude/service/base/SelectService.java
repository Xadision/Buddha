package com.jimi.bude.service.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jimi.bude.exception.ParameterException;
import com.jimi.bude.model._MappingKit;

import cc.darhao.dautils.api.StringUtil;


/**
 * 通用查询业务层
 * <br>
 * <b>2018年5月23日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class SelectService {

	public static void main(String[] args) {
		PropKit.use("config.properties");
		DruidPlugin dp = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"), PropKit.get("driver"));
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		arp.setDialect(new MysqlDialect()); // 用什么数据库，就设置什么Dialect
		arp.setShowSql(true);
		_MappingKit.mapping(arp);
		dp.start();
		arp.start();
		SelectService selectService = new SelectService();
		String result = selectService.select("bead", null, null, null, null, null, null).getList().toString();
		System.out.println(result);
		result = selectService.select(new String[] {"head", "face", "bead"}, new String[] {"head.id=face.head_id", "face.id = bead.face_id"}, 1, 30, null, null, null, new String[] {"head.id", "face.id"}).getList().toString();
		System.out.println(result);
	}


	/**
	 * 分页查询，支持筛选和排序
	 * @param tables 提供可读的表名数组
	 * @param refers 外键数组，单表可为null
	 * @param pageNo 页码，从1开始
	 * @param pageSize 每页的条目数
	 * @param ascBy 按指定字段升序，不可和descBy同时使用
	 * @param descBy 按指定字段降序，不可和ascBy同时使用
	 * @param filter 按字段筛选，支持<, >, >,=, <=, !=, =，多个字段请用&隔开
	 * @param discard 表中不要的字段,最好将关联的外键去掉，只保留一个
	 * @return Page对象
	 */
	public Page<Record> select(String[] tables, String[] refers, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter, String[] discard) {
		StringBuffer sql = new StringBuffer();
		List<String> questionValues = new ArrayList<>();
		createFrom(tables, refers, sql);
		createWhere(filter, questionValues, sql);
		createOrderBy(ascBy, descBy, sql);
		return paginateAndFillWhereValues(tables, pageNo, pageSize, sql, questionValues, discard);
	}


	/**
	 * 分页查询，支持筛选和排序
	 * @param table 提供可读的表名
	 * @param pageNo 页码，从1开始
	 * @param pageSize 每页的条目数
	 * @param ascBy 按指定字段升序，不可和descBy同时使用
	 * @param descBy 按指定字段降序，不可和ascBy同时使用
	 * @param filter 按字段筛选，支持<, >, >,=, <=, !=, =，多个字段请用&隔开
	 * @return Page对象
	 */
	public Page<Record> select(String table, Integer pageNo, Integer pageSize, String ascBy, String descBy, String filter, String[] discard) {
		return select(new String[] {table}, null, pageNo, pageSize, ascBy, descBy, filter, discard);
	}


	private void createFrom(String[] tables, String[] refers, StringBuffer sql) {
		// 表名非空判断
		if (tables == null) {
			throw new ParameterException("table name must be provided");
		}
		// 创建FROM
		sql.append(" FROM ");
		// 表是否在可读范围内
		String[] readabledTables = PropKit.use("config.properties").get("readableTables").split(",");
		int pass = 0;
		for (String table : tables) {
			for (String readabledTable : readabledTables) {
				if (readabledTable.equals(table)) {
					pass++;
					sql.append(table + " JOIN ");
					break;
				}
			}
		}
		if (pass != tables.length) {
			throw new ParameterException("some tables are not readabled");
		}
		sql.delete(sql.lastIndexOf("JOIN"), sql.length());
		// 创建ON
		if (refers != null) {
			sql.append(" ON ");
			for (String refer : refers) {
				sql.append(refer);
				sql.append(" AND ");
			}
			sql.delete(sql.lastIndexOf("AND"), sql.length());
		}
	}


	private void createWhere(String filter, List<String> questionValues, StringBuffer sql) {
		// 判断filter存在与否
		if (filter != null) {
			sql.append(" WHERE ");
			String[] whereUnits = filter.split("&");
			int index = 0;
			for (String whereUnit : whereUnits) {
				// 分割键值与运算符
				int operatorStartIndex = -1;
				StringBuffer operator = new StringBuffer();
				for (int i = 0; i < whereUnit.length(); i++) {
					char c = whereUnit.charAt(i);
					if (c == '>' || c == '<' || c == '=' || c == '!') {
						operator.append(c);
						if (operatorStartIndex == -1) {
							operatorStartIndex = i;
						}
					}
				}
				String key = whereUnit.substring(0, operatorStartIndex);
				String value = whereUnit.substring(operatorStartIndex + operator.length(), whereUnit.length());
				sql.append(key + operator.toString() + "? AND ");
				questionValues.add(value.trim());
				if (index == whereUnits.length - 1) {
					sql.delete(sql.lastIndexOf("AND"), sql.length());
				}
				index++;
			}
		}
	}


	private void createOrderBy(String ascBy, String descBy, StringBuffer sql) {
		if (ascBy != null && descBy != null) {
			throw new ParameterException("ascBy and descBy can not be provided at the same time");
		} else if (ascBy != null) {
			sql.append(" ORDER BY " + ascBy + " ASC ");
		} else if (descBy != null) {
			sql.append(" ORDER BY " + descBy + " DESC ");
		}
	}


	private Page<Record> paginateAndFillWhereValues(String[] tables, Integer pageNo, Integer pageSize, StringBuffer sql, List<String> questionValues, String[] discard) {
		if ((pageNo != null && pageSize == null) || (pageNo == null && pageSize != null)) {
			throw new ParameterException("pageNo and pageSize must be provided at the same time");
		}
		String resultSet = createResultSet(tables, discard);
		if (pageNo == null && pageSize == null) {
			return Db.paginate(1, PropKit.use("config.properties").getInt("defaultPageSize"), resultSet, sql.toString(), questionValues.toArray());
		} else {
			return Db.paginate(pageNo, pageSize, resultSet, sql.toString(), questionValues.toArray());
		}
	}


	private String createResultSet(String[] tables, String[] discards) {
		/*
		 * if(tables.length == 1) { return "SELECT *"; }
		 */
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		PropKit.use("config.properties");
		String baseModelPackage = PropKit.get("baseModelPackage");
		for (String table : tables) {
			try {
				String beanClassName = StrKit.firstCharToUpperCase(StringUtil.trimUnderLineAndToUpCase(table));
				Class<?> beanClass = Class.forName(baseModelPackage + ".Base" + beanClassName);
				Method[] methods = beanClass.getMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					if (methodName.startsWith("set") == false || methodName.length() <= 3) {
						continue;
					}
					Class<?>[] types = method.getParameterTypes();
					if (types.length != 1) {
						continue;
					}
					String attrName = methodName.substring(3);
					String colName = StringUtil.toLowCaseAndInsertUnderLine(StrKit.firstCharToLowerCase(attrName));
					String name = table + "." + colName;
					if (discards != null && discards.length > 0) {
						for (String discard : discards) {
							if (name.equals(discard)) {
								name = "";
							}
						}
					}
					if (!name.equals("")) {
						sql.append(name + " AS " + beanClassName + "_" + attrName);
						sql.append(",");
					}

				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		sql.delete(sql.lastIndexOf(","), sql.length());
		return sql.toString();
	}

}
