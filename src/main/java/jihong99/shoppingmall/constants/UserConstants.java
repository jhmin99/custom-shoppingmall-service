package jihong99.shoppingmall.constants;

public class UserConstants {

    // Constructor restriction
    private UserConstants(){
    }

    public static final String  STATUS_201 = "201";
    public static final String  MESSAGE_201_createUser = "User has been created successfully.";
    public static final String  STATUS_200 = "200";
    public static final String  MESSAGE_200_verifiedId = "The ID is available for use.";

    public static final String  STATUS_400 = "400";
    public static final String  MESSAGE_400_duplicatedId = "The ID already exists.";
    public static final String  MESSAGE_400_MissMatchPw = "Passwords do not match.";
    public static final String  MESSAGE_400_WrongBirthDate = "Invalid birth date.";
}
