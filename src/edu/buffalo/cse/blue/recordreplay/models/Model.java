package edu.buffalo.cse.blue.recordreplay.models;

public class Model {

	// Global constants
	public static final String INT = " INT";
	public static final String REAL = " REAL";
	public static final String TEXT = " TEXT";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final String PK = " PRIMARY KEY";
	public static final String AUTO_INC = " AUTOINCREMENT";
	public static final String FK = " FOREIGN KEY";
	public static final String REFS = " REFERENCES ";
	public static final String OPEN_PAREN = " ( ";
	public static final String CLOSE_PAREN = " ) ";
	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	
	protected static String dropTable(String tableName) {
		return DROP_TABLE + tableName;
	}

}
