package jihong99.shoppingmall.constants;

public class Constants {

    // Constructor restriction
    private Constants(){
    }

    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201_createUser = "User has been created successfully.";
    public static final String MESSAGE_201_createDeliveryAddress = "DeliveryAddress has been created successfully.";

    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200_verifiedId = "The ID is available for use.";
    public static final String MESSAGE_200_fetchSuccess = "Fetch success";
    public static final String MESSAGE_200_LoginSuccess = "Logged in successfully.";
    public static final String MESSAGE_200_LogoutSuccess = "Logged out successfully.";
    public static final String MESSAGE_200_UpdateDeliveryAddressSuccess = "Delivery address updated successfully.";
    public static final String MESSAGE_200_DeleteDeliveryAddressSuccess = "Delivery address deleted successfully.";
    public static final String MESSAGE_200_UpdateItemSuccess = "Item updated successfully.";
    public static final String MESSAGE_200_DeleteItemSuccess = "Item deleted successfully.";
    public static final String MESSAGE_400_duplicatedId = "The ID already exists.";
    public static final String MESSAGE_400_MisMatchPw = "Passwords do not match.";
    public static final String MESSAGE_400_InvalidRefreshToken = "Refresh Token is invalid or not found";
    public static final String MESSAGE_404_UserNotFound = "User not found.";
    public static final String MESSAGE_404_CategoryNotFound = "Category not found.";
    public static final String MESSAGE_404_ItemNotFound = "Item not found.";
    public static final String MESSAGE_404_DeliveryAddressNotFound = "Delivery address not found.";
}
