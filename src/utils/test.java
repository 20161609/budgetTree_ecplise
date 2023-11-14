package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class test {
	public static void main(String[] args) {
		
		String a = "3";
		int b = Integer.parseInt(a);
		System.out.println("SEX1");
//		System.out.println(b);

		String url = "jdbc:h2:~/test"; // 데이터베이스 URL
		String user = "sa"; // 사용자 이름
		String password = ""; // 비밀번호

		// 연결 시도
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("SEX");
			System.out.println(conn);
		}catch(Exception e) {
			System.out.println("FUCK YOU");
		}

	
	}
}
