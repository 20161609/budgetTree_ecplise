package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DataHandler {
    public static String getCurrentFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return now.format(formatter);
    }

    public static boolean isValidDate(String strDate) {
        // 엄격한 날짜 형식과 파싱 스타일로 DateTimeFormatter를 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            // 문자열을 LocalDate 객체로 파싱 시도
            LocalDate.parse(strDate, formatter);
            return true; // 파싱 성공 시 true 반환
        } catch (DateTimeParseException e) {
            return false; // 파싱 실패 시 false 반환
        }
    }
    
    public static boolean compareDates(String beginDate, String endDate) {
    	String[] beginSplited = beginDate.split("-",0);
    	String[] endSplited = endDate.split("-",0);
    	
    	int beginValue = 0, endValue = 0;
    	for(int i = 0; i < 3; i++) {
    		beginValue *= 1000;    		
    		beginValue += Integer.parseInt(beginSplited[i]);
    		
    		endValue *= 1000;		
    		endValue += Integer.parseInt(endSplited[i]);
    	}
    	return beginValue <= endValue;
    }
	


}



