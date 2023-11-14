package transactionController;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {
	
	
//	public Connection con = getConnection();
	public static HashSet<String> default_fields = new HashSet<>(Arrays.asList("transaction_date", "cashFlow", "branch", "updateTime","id"));
	public static void main(String[] args) {
		
	//	System.out.println("FUCK YOU SEX1");
//        System.out.println("FUCK YOU SEX2");
//        dropTable(con);
//		createTable(con);
//		insertData(con);
//		Connection con = getConnection();
//		abc(con);
		
//        addColumn(con, "transactions", "attr1", "INT");
//        dropColumn(con, "transactions", "id2");

//        ArrayList<ArrayList<String>> fields = collectFields(con);
  //      System.out.println(fields);
//		printTableData(con);
    //    System.out.println("FUCK YOU SEX3");
	}

    public ArrayList<ArrayList<String>> fetchDataFromDatabase(Connection con, String query) {
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
	
    public static void abc(Connection con) {
        Statement stmt = null;
        try {
            // Change the data type of the transaction_date column to DATE
            String sql = "ALTER TABLE transactions MODIFY transaction_date DATE;";
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Column data type modified successfully");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public List<Map<String, Object>> executeQuery(Connection con, String query) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnType = metaData.getColumnTypeName(i);
                    Object value = rs.getObject(i);

                    // 키를 "columnName (columnType)"의 형태로 저장합니다.
                    row.put(columnName + " (" + columnType + ")", value);
                }
                resultList.add(row);
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

        return resultList;
    }
    
	public static void addColumn(Connection con, String tableName, String columnName, String dataType) {
	    Statement stmt = null;
	    try {
	        // SQL 명령을 준비합니다.
	        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType;
	        
	        // Statement를 생성하고 SQL 명령을 실행합니다.
	        stmt = con.createStatement();
	        stmt.executeUpdate(sql);

	        System.out.println("Column " + columnName + " added successfully to table " + tableName);
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	        // 자원을 해제합니다.
	        try {
	            if(stmt != null) stmt.close();
	        } catch(SQLException se2) {
	            se2.printStackTrace();
	        }
	    }
	}
	
	public static void dropColumn(Connection con, String tableName, String columnName) {
		if(default_fields.contains(columnName)) {
			System.out.println("EIEHEI");
		}
		
	    Statement stmt = null;
	    try {
	        // SQL 명령을 준비합니다.
	        String sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	        
	        // Statement를 생성하고 SQL 명령을 실행합니다.
	        stmt = con.createStatement();
	        stmt.executeUpdate(sql);

	        System.out.println("Column " + columnName + " dropped successfully from table " + tableName);
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    } finally {
	        // 자원을 해제합니다.
	        try {
	            if(stmt != null) stmt.close();
	        } catch(SQLException se2) {
	            se2.printStackTrace();
	        }
	    }
	}
	
	public ArrayList<ArrayList<String>> collectFields(Connection con) {
        ArrayList<ArrayList<String>> fields = new ArrayList<>();
	    ResultSet rs = null;
	    Statement stmt = null;
	    try {
	        // 테이블 구조를 확인하는 SQL 명령
//	        String sql = "DESCRIBE transactions";
	        String sql = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'TRANSACTIONS'";

	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);

//	        System.out.println("Field\tType\tNull\tKey\tDefault\tExtra");
	        while(rs.next()) {
	            // 각 필드의 상세 정보 출력
	            ArrayList<String> newRow = new ArrayList<>();
	            newRow.add(rs.getString("COLUMN_NAME"));
	            newRow.add(rs.getString("TYPE_NAME"));
	            fields.add(newRow);	            
	        }
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
	
	public static void describeTable(Connection con, String tableName) {
	    ResultSet rs = null;
	    Statement stmt = null;
	    try {
	        // 테이블 구조를 확인하는 SQL 명령
	        String sql = "DESCRIBE " + tableName;

	        stmt = con.createStatement();
	        rs = stmt.executeQuery(sql);

	        System.out.println("Field\tType\tNull\tKey\tDefault\tExtra");
	        while(rs.next()) {
	            // 각 필드의 상세 정보 출력
	            String field = rs.getString("Field");
	            String type = rs.getString("Type");
	            String nullAllowed = rs.getString("Null");
	            String key = rs.getString("Key");
	            String defaultValue = rs.getString("Default");
	            String extra = rs.getString("Extra");
	            
	            System.out.printf("%s\t%s\t%s\t%s\t%s\t%s\n", field, type, nullAllowed, key, defaultValue, extra);
	        }
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
	}
	
	public static void printTableData(Connection con) {
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
	        stmt = con.createStatement();
	        rs = stmt.executeQuery("SELECT * FROM transactions");
	        
	        while (rs.next()) {
	            // 각 컬럼의 데이터를 가져옵니다. 컬럼명은 실제 테이블의 컬럼명과 일치해야 합니다.
	            Date transaction_date = rs.getDate("transaction_date");
	            int cashFlow = rs.getInt("cashFlow");
	            String branch = rs.getString("branch");
	            Timestamp updateTime = rs.getTimestamp("updateTime");
	            
	            // 가져온 데이터를 출력합니다.
	            System.out.println("transaction_date: " + transaction_date + ", Cash Flow: " + cashFlow + 
	                               ", Branch: " + branch + ", Update Time: " + updateTime);
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
	}
	
	public static void dropTable(Connection con) {
	    try {
	        // transactions 테이블을 삭제하는 SQL 명령어
	        String sql = "DROP TABLE IF EXISTS transactions";

	        PreparedStatement dropStmt = con.prepareStatement(sql);
	        dropStmt.execute();

	        System.out.println("Table dropped successfully");
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    }
	}

	public static void createTable(Connection con) {
	    try {
	        // id 필드를 추가하고 AUTO_INCREMENT 속성을 설정하여 자동으로 증가하도록 합니다.
	        // id 필드를 PRIMARY KEY로 설정합니다.
	        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
	                     "id INT NOT NULL AUTO_INCREMENT, " +
	                     "`transaction_date` DATE, " +
	                     "cashFlow INTEGER CHECK (cashFlow BETWEEN -100000000 AND 100000000), " +
	                     "branch VARCHAR(255), " +
	                     "updateTime TIMESTAMP, " + 
	                     "PRIMARY KEY (id)" +  // id를 PRIMARY KEY로 설정
	                     ")";
	        
	        PreparedStatement create = con.prepareStatement(sql);
	        create.execute();
	        System.out.println("Table Successfully created");
	    } catch(Exception e) {
	        System.out.println(e.getMessage());
	    }
	}

	public static void insertData(Connection con) {
	    try {
	        PreparedStatement insert = con.prepareStatement(
	                "INSERT INTO transactions (`transaction_date`, branch, cashFlow, updateTime) " +
	                "VALUES ('2023-03-23', 'enk', 1234, '2023-11-08 13:45:00')");
	        insert.execute();
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	public void deleteData(Connection con, String id) {
        String sql = "DELETE FROM transactions WHERE id =" + id;
	    try {
	        PreparedStatement insert = con.prepareStatement(sql);
	        insert.execute();
	        System.out.println("Deletion Success");
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	public static Connection getConnection() {
		try {
			String url = "jdbc:h2:~/test"; // 데이터베이스 URL
			String user = "sa"; // 사용자 이름
			String password = ""; // 비밀번호
			
			// 연결 시도
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("The Connection Succesful!");
			return conn;
		}catch(Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
}