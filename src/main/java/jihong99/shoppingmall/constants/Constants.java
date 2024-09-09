package jihong99.shoppingmall.constants;

public class Constants {


    // Constructor restriction
    private Constants(){
    }


    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200_verifiedId = "The ID is available for use.";
    public static final String MESSAGE_200_LogoutSuccess = "Logged out successfully.";
    public static final String MESSAGE_200_UpdateAdminSuccess = "Admin account updated successfully.";
    public static final String MESSAGE_200_UpdateUserSuccess = "User account updated successfully.";
    public static final String MESSAGE_200_UpdateCategorySuccess = "Category updated successfully.";
    public static final String MESSAGE_200_DeleteCategorySuccess = "category deleted successfully.";

    public static final String MESSAGE_200_DeleteAdminSuccess = "Admin account deleted successfully.";
    public static final String MESSAGE_200_UpdateDeliveryAddressSuccess = "Delivery address updated successfully.";
    public static final String MESSAGE_200_DeleteDeliveryAddressSuccess = "Delivery address deleted successfully.";
    public static final String MESSAGE_200_UpdateItemSuccess = "Item updated successfully.";
    public static final String MESSAGE_200_InvalidateItemSuccess = "Item invalidated successfully.";
    public static final String MESSAGE_200_DistributeCouponSuccess = "Coupon distributed sucessfully.";
    public static final String MESSAGE_200_UpdateCouponSuccess = "Coupon updated successfully.";
    public static final String MESSAGE_200_DeleteCouponSuccess = "Coupon deleted successfully.";

    public static final String MESSAGE_200_DistributeNoticeSuccess = "Notice distributed successfully.";

    public static final String MESSAGE_200_UpdateNoticeSuccess = "Notice updated successfully.";

    public static final String MESSAGE_200_DeleteNoticeSuccess = "Notice deleted successfully.";

    public static final String MESSAGE_200_NotifyUsersSuccess = "Notify users successfully";
    public static final String MESSAGE_200_UpdateRespondSuccess = "Respond updated successfully.";
    public static final String MESSAGE_200_DeleteRespondSuccess = "Respond deleted successfully.";
    public static final String MESSAGE_200_UpdateInquiryStatusSuccess = "Inquiry status updated successfully.";
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201_createUser = "User has been created successfully.";
    public static final String MESSAGE_201_createDeliveryAddress = "DeliveryAddress has been created successfully.";
    public static final String MESSAGE_201_createCategory = "Category has been created successfully.";
    public static final String MESSAGE_201_createItem = "Item has been created successfully.";
    public static final String MESSAGE_201_createCoupon = "Coupon has been created successfully.";
    public static final String MESSAGE_201_createNotice = "Notice has been created successfully.";
    public static final String MESSAGE_201_createRespondSuccess = "Respond to Inquiry has been created successfully.";

    public static final String MESSAGE_400_duplicatedId = "The ID already exists.";
    public static final String MESSAGE_400_duplicatedName = "The name already exists.";
    public static final String MESSAGE_400_MisMatchPw = "Passwords do not match.";
    public static final String MESSAGE_400_InvalidRefreshToken = "Refresh Token is invalid or not found";

    public static final String MESSAGE_400_InvalidStorageType = "Unknown storage type: ";
    public static final String MESSAGE_404_UserNotFound = "User not found.";
    public static final String MESSAGE_404_CategoryNotFound = "Category not found.";
    public static final String MESSAGE_404_ItemNotFound = "Item not found.";
    public static final String MESSAGE_404_DeliveryAddressNotFound = "Delivery address not found.";
    public static final String MESSAGE_404_CouponNotFound = "Coupon not found.";
    public static final String MESSAGE_404_NoticeNotFound = "Notice not found.";
    public static final String MESSAGE_404_InquiryNotFound = "Inquiry not found.";
    public static final String MESSAGE_404_ImageNotFound = "Image not found.";
    public static final String MESSAGE_404_OrdersNotFound = "Orders not found.";

    public static final String MESSAGE_404_ResponseNotFound = "Response not found.";

    public static final String MESSAGE_409_RelationConflict = "Relation conflict. deletion not allowed.";

    public static final String MESSAGE_500_ImageUploadFailed = "Failed to upload image.";
}
