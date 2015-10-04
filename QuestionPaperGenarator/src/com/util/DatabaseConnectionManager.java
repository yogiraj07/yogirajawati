package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager implements DBConnectionUtil {

Connection conn;
	
	public DatabaseConnectionManager() { /*does nothing*/ }	
	
	@Override
	public Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/questiondb","root","root");
		
		
		if(!conn.isClosed())
			System.out.println("Connection successfull..!!!!");
		
		return conn;
		
	}
	
	@Override
	public void closeConnection() throws SQLException
	{
		conn.close();
	}


}
