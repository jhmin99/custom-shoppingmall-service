package jihong99.shoppingmall.service;

import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.request.coupon.CouponRequestDto;
import jihong99.shoppingmall.dto.request.coupon.PatchCouponRequestDto;
import jihong99.shoppingmall.dto.response.coupon.CouponDetailsResponseDto;
import jihong99.shoppingmall.dto.response.coupon.CouponResponseDto;
import jihong99.shoppingmall.dto.response.coupon.CouponSummaryResponseDto;
import jihong99.shoppingmall.dto.response.coupon.UserCouponsResponseDto;
import jihong99.shoppingmall.entity.Cart;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.UserCoupon;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.DiscountType;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CartRepository;
import jihong99.shoppingmall.repository.CouponRepository;
import jihong99.shoppingmall.repository.UserCouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final CartRepository cartRepository;

    /**
     * Retrieves the coupons of a specific user.
     *
     * @param userId The ID of the user.
     * @param pageable Pagination information.
     * @return A paginated list of the user's coupons.
     */
    @Override
    public Page<UserCouponsResponseDto> getUserCoupons(Long userId, Pageable pageable) {
        return userCouponRepository.findAll(pageable)
                .map(CouponServiceImpl::convertToUserCouponsResponseDto);
    }


    /**
     * Creates a new coupon.
     *
     * @param couponRequestDto The DTO containing the coupon details.
     */
    @Override
    @Transactional
    public void createCoupon(CouponRequestDto couponRequestDto) {
        Coupon coupon = Coupon.createCoupon(
                couponRequestDto.getDiscountType(),
                couponRequestDto.getDiscountValue(),
                couponRequestDto.getExpirationDate()
        );
        couponRepository.save(coupon);
    }

    /**
     * Retrieves all coupons with pagination.
     *
     * @param pageable Pagination information.
     * @return A paginated list of all coupons.
     */
    @Override
    public Page<CouponResponseDto> getAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable)
                .map(CouponServiceImpl::convertToCouponResponseDto);
    }


    /**
     * Distributes a coupon to a specific user.
     *
     * @param couponId The ID of the coupon to be distributed.
     * @param userId The ID of the user to whom the coupon will be distributed.
     */
    @Override
    @Transactional
    public void distributeCouponToUser(Long couponId, Long userId) {
        Coupon coupon = findCouponOrThrow(couponId);
        Users user = findUserOrThrow(userId);
        UserCoupon userCoupon = UserCoupon.createUserCoupon(user, coupon);
        userCouponRepository.save(userCoupon);
    }

    /**
     * Distributes a coupon to all users.
     *
     * @param couponId The ID of the coupon to be distributed.
     */
    @Override
    @Transactional
    public void distributeCouponToAllUsers(Long couponId) {
        Coupon coupon = findCouponOrThrow(couponId);
        List<Users> users = userRepository.findAllByRole(Roles.USER);
        List<UserCoupon> userCoupons = createUserCoupons(users, coupon);
        userCouponRepository.saveAll(userCoupons);
    }

    /**
     * Deletes a coupon and all its associations with users.
     *
     * @param couponId The ID of the coupon to be deleted.
     */
    @Override
    @Transactional
    public void deleteCoupon(Long couponId) {
        Coupon coupon = findCouponOrThrow(couponId);
        userCouponRepository.deleteAllByCouponId(coupon.getId());
        couponRepository.delete(coupon);
    }

    /**
     * Updates a coupon's details.
     *
     * @param couponId The ID of the coupon to be updated.
     * @param patchCouponRequestDto The DTO containing the updated coupon details.
     */
    @Override
    @Transactional
    public void patchCoupon(Long couponId, PatchCouponRequestDto patchCouponRequestDto) {
        Coupon coupon = findCouponOrThrow(couponId);
        if (patchCouponRequestDto.getDiscountType() != null) {
            coupon.updateDiscountType(patchCouponRequestDto.getDiscountType());
        }
        if (patchCouponRequestDto.getDiscountValue() != null) {
            coupon.updateDiscountValue(patchCouponRequestDto.getDiscountValue());
        }
        if (patchCouponRequestDto.getExpirationDate() != null) {
            coupon.updateExpirationDate(patchCouponRequestDto.getExpirationDate());
        }
        couponRepository.save(coupon);
    }
    /**
     * Retrieves all coupon summaries for a user and updates expired coupons to invalid.
     *
     * <p>This method checks each coupon's expiration date and updates its status to invalid if it has expired.</p>
     *
     * @param userId The ID of the user.
     * @param status The status filter for coupons (e.g., 'valid', 'invalid').
     * @param pageable Pagination information.
     * @return A paginated list of coupon summaries.
     */
    @Transactional
    @Override
    public Page<CouponSummaryResponseDto> getAllCouponSummaries(Long userId, String status, Pageable pageable) {
        Users user = findUserOrThrow(userId);
        Page<UserCoupon> userCoupons = userCouponRepository.findAllByUsersIdAndStatus(user.getId(), status, pageable);

        userCoupons.forEach(userCoupon -> {
            if (userCoupon.getCoupon().getExpirationDate().isBefore(LocalDate.now())) {
                userCoupon.updateToInvalid();
                userCouponRepository.save(userCoupon);
            }
        });

        return userCoupons.map(this::convertToCouponSummaryResponseDto);
    }

    /**
     * Retrieves detailed information of a specific coupon for a user.
     *
     * @param userId The ID of the user.
     * @param couponId The ID of the coupon.
     * @return CouponDetailsResponseDto with coupon details.
     */
    @Transactional(readOnly = true)
    @Override
    public CouponDetailsResponseDto getCouponDetails(Long userId, Long couponId) {
        UserCoupon userCoupon = findUserCouponOrThrow(userId, couponId);
        return convertToCouponDetailsResponseDto(userCoupon);
    }

    /**
     * Applies a coupon to a user's cart, adjusting the total price.
     *
     * @param userId The ID of the user.
     * @param couponId The ID of the coupon to be applied.
     */
    @Override
    @Transactional
    public void applyCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = findUserCouponOrThrow(userId, couponId);
        Users user = findUserOrThrow(userId);
        Cart cart = findCartOrThrow(user);
        if (!userCoupon.getIsValid() || userCoupon.getIsUsed()) {
            throw new InvalidOperationException(Constants.MESSAGE_400_InvalidOrUsedCoupon);
        }
        cart.updateAppliedCoupon(userCoupon.getCoupon());
        cart.recalculateTotalPrices();
        cartRepository.save(cart);
    }


    private Cart findCartOrThrow(Users user) {
        return cartRepository.findById(user.getId()).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_CartNotFound)
        );
    }

    private UserCoupon findUserCouponOrThrow(Long userId, Long couponId) {
        return userCouponRepository.findByUsersIdAndCouponId(userId, couponId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_UserCouponNotFound)
        );
    }
    
    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_UserNotFound)
        );
    }

    private Coupon findCouponOrThrow(Long couponId) {
        return couponRepository.findById(couponId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_CouponNotFound)
        );
    }
    private static List<UserCoupon> createUserCoupons(List<Users> users, Coupon coupon) {
        return users.stream()
                .map(user -> UserCoupon.createUserCoupon(user, coupon))
                .collect(Collectors.toList());
    }
    private static UserCouponsResponseDto convertToUserCouponsResponseDto(UserCoupon userCoupon) {
        return UserCouponsResponseDto.of(
                userCoupon.getCoupon().getId(),
                userCoupon.getCoupon().getCode(),
                userCoupon.getCoupon().getDiscountType(),
                userCoupon.getCoupon().getDiscountValue(),
                userCoupon.getCoupon().getExpirationDate(),
                userCoupon.getIsValid(),
                userCoupon.getIsUsed(),
                userCoupon.getCreationTime(),
                userCoupon.getLastModifiedTime()
        );
    }

    private static CouponResponseDto convertToCouponResponseDto(Coupon coupon) {
        return CouponResponseDto.of(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getCreationTime(),
                coupon.getLastModifiedTime());
    }

    private CouponSummaryResponseDto convertToCouponSummaryResponseDto(UserCoupon userCoupon) {
        return CouponSummaryResponseDto.of(
                userCoupon.getId(),
                userCoupon.getCoupon().getCode(),
                userCoupon.getCoupon().getDiscountType(),
                userCoupon.getCoupon().getDiscountValue(),
                userCoupon.getCoupon().getExpirationDate()
        );
    }


    private static CouponDetailsResponseDto convertToCouponDetailsResponseDto(UserCoupon userCoupon) {
        return CouponDetailsResponseDto.of(
                userCoupon.getId(),
                userCoupon.getCoupon().getCode(),
                userCoupon.getCoupon().getDiscountType(),
                userCoupon.getCoupon().getDiscountValue(),
                userCoupon.getIsValid(),
                userCoupon.getIsUsed()
        );
    }



    private void applyCouponDiscount(Cart cart, Coupon coupon) {
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            cart.setEstimatedTotalPrice(cart.getOriginalTotalPrice() - calculatePercentageDiscount(cart, coupon));
        } else if (coupon.getDiscountType() == DiscountType.FIXED) {
            cart.setEstimatedTotalPrice(cart.getOriginalTotalPrice() - coupon.getDiscountValue());
        }
    }

    private Long calculatePercentageDiscount(Cart cart, Coupon coupon) {
        return cart.getOriginalTotalPrice() * coupon.getDiscountValue() / 100;
    }
}
