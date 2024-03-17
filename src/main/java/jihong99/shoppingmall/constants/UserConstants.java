package jihong99.shoppingmall.constants;

public class UserConstants {

    // 생성자 제한
    private UserConstants(){
    }

    public static final String  STATUS_201 = "201";
    public static final String  MESSAGE_201_createUser = "회원이 정상적으로 생성되었습니다.";
    public static final String  STATUS_200 = "200";
    public static final String  MESSAGE_200_verifiedId = "아이디를 사용가능합니다.";

    public static final String  STATUS_400 = "400";
    public static final String  MESSAGE_400_duplicatedId = "중복된 아이디가 존재합니다.";
    public static final String  MESSAGE_400_MissMatchPw = "비밀번호가 일치하지 않습니다.";
    public static final String  MESSAGE_400_WrongBirthDate = "생년월일이 올바르지 않습니다.";

}
