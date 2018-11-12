package com.tl.job002.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * mysql 数据库工具类
 * 
 * @author lihonghao
 * @date 2018年11月10日
 */
public class DataBaseUtil {
	private String driver;
	private String url;
	private String username;
	private String password;
	private Connection dbConn;

	public Connection getDbConn() {
		return dbConn;
	}

	public void setDbConn(Connection dbConn) {
		this.dbConn = dbConn;
	}

	public DataBaseUtil(String driver, String url, String username, String password)
			throws ClassNotFoundException, SQLException {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		init();
	}

	public ResultSet getResultSetByStat(String query) throws SQLException {
		PreparedStatement stat = dbConn.prepareStatement(query);
		return stat.executeQuery(query);
	}

	public ResultSet getResultSet(String query) throws SQLException {
		Statement stat = dbConn.createStatement();
		return stat.executeQuery(query);
	}

	public PreparedStatement getPreparedStatement(String sql) throws SQLException {
		return dbConn.prepareStatement(sql);
	}

	public void init() throws SQLException, ClassNotFoundException {
		Class.forName(this.driver);
		dbConn = DriverManager.getConnection(url, username, password);
	}

	public void close() throws SQLException {
		dbConn.close();
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int insert(String sql) throws SQLException {
		PreparedStatement ps = dbConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		return result;
	}

	public int updata(String sql) throws SQLException {
		PreparedStatement ps = dbConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		return result;
	}

	public int delet(String sql) throws SQLException {
		PreparedStatement ps = dbConn.prepareStatement(sql);
		int result = ps.executeUpdate();
		return result;
	}

	public ResultSet select(String sql) throws SQLException {
		PreparedStatement ps = dbConn.prepareStatement(sql);
		ResultSet result = ps.executeQuery(sql);
		return result;
	}

	public boolean load(String sql) throws SQLException {
		PreparedStatement ps = dbConn.prepareStatement(sql);
		boolean result = ps.execute();
		return result;
	}
}
