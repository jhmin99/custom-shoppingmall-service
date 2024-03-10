package jihong99.shoppingmall.utils;


import java.time.LocalDate;

public class BirthDateParser {

    // 생년월일을 String -> LocalDate 로 변환
    public static LocalDate birthDateStrToLocalDate(String birthDateStr){
        return LocalDate.parse(birthDateStr);
    }
}
