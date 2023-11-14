package transactionController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import utils.DataHandler;

public class DBclass {
	public static HashSet<String> default_fields = new HashSet<>(Arrays.asList("TRANSACTION_DATE", "CASHFLOW", "BRANCH", "UPDATETIME","ID"));	
	public static Connection getConnection() {
		try {
			String url = "jdbc:h2:~/test";
			String user = "sa";
			String password = "";
			
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("The Connection Succesful!");
			return conn;
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
    public static ArrayList<ArrayList<String>> fetchDataFromDatabase(Connection con, String query) {
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<ArrayList<String>> cols = new ArrayList<>();        
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                ArrayList<String> new_col = new ArrayList<>();
            	for(int i = 1; i <= columnCount; i++) {
            		Object value = rs.getObject(i);
            		if(value==null) {
            			new_col.add("null");
            		}else {
            			new_col.add(value.toString());
            		}
            	}
            	cols.add(new_col);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // ResultSet과 Statement 리소스를 안전하게 닫습니다.
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cols;
    }

	
	public static ArrayList<ArrayList<String>> collectFields(Connection con) {
        ArrayList<ArrayList<String>> fields = new ArrayList<>();
	    ResultSet rs = null;
	    Statement stmt = null;
	    try {
	        String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'TRANSACTIONS'";

	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);

	        while(rs.next()) {
	            ArrayList<String> newRow = new ArrayList<>();
	            newRow.add(rs.getString("column_name"));
	            newRow.add(rs.getString("type_name"));
	            fields.add(newRow);	            
	        }
	        System.out.println(fields);
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	        try {
	            if(rs != null) rs.close();
	            if(stmt != null) stmt.close();
	        } catch(Exception e) {
	            System.out.println(e.getMessage());
	        }
	    }
	    return fields;
	}

	public static void deleteData(Connection con, String id) {
        String sql = "DELETE FROM transactions WHERE id =" + id;
	    try {
	        PreparedStatement insert = con.prepareStatement(sql);
	        insert.execute();
	        System.out.println("Deletion Success");
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	public static void insertData(Connection con) {
    	int cashFlow = 0;
		if(input.length() > 0) {
			cashFlow += Integer.parseInt(input);
		}else {
			cashFlow -= Integer.parseInt(output);
		}

		String sql = "INSERT INTO transactions (transaction_date, branch, cashFlow, updateTime";
		for(int i = 0; i < extra_fields.size(); i++) {
			sql += ", " + extra_fields.get(i);
		}
		sql += ") ";

		sql += "VALUES (";
		sql += "'" + transaction_date + "'";
		sql += ",'" + branch + "'";
		sql += "," + String.valueOf(cashFlow);
		sql += ",'" + DataHandler.getCurrentFormattedTime() + "'";
		for(int i = 0; i < extra_values.size(); i++) {
			if(extra_values.get(i).length() == 0) {
				sql += ", NULL";
			}else {
				String type = typeDictionary.get(extra_fields.get(i));
				if(type.substring(0,3) == "int") {
					sql += ", " + extra_values.get(i);
				}else {
					sql += ", '" + extra_values.get(i) + "'";
				}				
			}
		}
		sql += ") ";
		System.out.println(sql);
		try {			
	        PreparedStatement insert = con.prepareStatement(sql);
	        insert.execute();
			System.out.println("Addition Success");
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
}
