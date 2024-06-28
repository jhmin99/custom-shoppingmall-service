package jihong99.shoppingmall.constants;

public class UserConstants {

    // Constructor restriction
    private UserConstants(){
    }

    public static final String  STATUS_201 = "201";
    public static final String  MESSAGE_201_createUser = "User has been created successfully.";
    public static final String  MESSAGE_201_createDeliveryAddress = "DeliveryAddress has been created successfully.";
    public static final String  STATUS_200 = "200";
    public static final String  MESSAGE_200_verifiedId = "The ID is available for use.";
    public static final String  MESSAGE_200_fetchSuccess = "Fetch success";
    public static final String MESSAGE_200_LoginSuccess = "Logged in successfully.";
    public static final String MESSAGE_200_LogoutSuccess = "Logged out successfully.";
    public static final String MESSAGE_200_UpdateDeliveryAddressSuccess = "Delivery address updated successfully.";
    public static final String MESSAGE_200_DeleteDeliveryAddressSuccess = "Delivery address deleted successfully.";

    public static final String  STATUS_400 = "400";
    public static final String  MESSAGE_400_duplicatedId = "The ID already exists.";
    public static final String  MESSAGE_400_MissMatchPw = "Passwords do not match.";
    public static final String  MESSAGE_400_WrongBirthDate = "Invalid birth date.";
    public static final String  MESSAGE_400_LoginFailed = "Login failed.";
    public static final String MESSAGE_400_LogoutFailed = "You are not logged in.";
    public static final String  STATUS_404 = "404";
    public static final String  MESSAGE_404_NoUserFound = "User not found.";
    public static final String MESSAGE_404_NoDeliveryAddressFound = "Delivery address not found.";
}
