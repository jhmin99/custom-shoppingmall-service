package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.dto.CouponResponseDto;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.UserCoupon;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.Tiers;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.InvalidExpirationDateException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.mapper.CouponMapper;
import jihong99.shoppingmall.repository.CouponRepository;
import jihong99.shoppingmall.repository.UserCouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService{

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * Retrieves a list of valid coupons for the specified user with pagination.
     * It first fetches the user's coupons, validates them, and then returns only the valid coupons.
     *
     * @param id the user's id
     * @param pageable pagination information
     * @return a paginated list of valid coupons
     * @throws NotFoundException if the user is not found
     */
    @Override
    @Transactional
    public Page<CouponResponseDto> getUserCoupons(Long id, Pageable pageable) {
        Users user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_UserNotFound));

        LocalDate today = LocalDate.now();
        Page<UserCoupon> paginatedUserCoupons = userCouponRepository.findByUsersId(user.getId(), pageable);
        validateCoupons(paginatedUserCoupons, today);
        List<CouponResponseDto> validCoupons = getValidCoupons(paginatedUserCoupons);
        return new PageImpl<>(validCoupons, pageable, paginatedUserCoupons.getTotalElements());
    }

    /**
     * Validates the user's coupons and marks expired ones as invalid.
     *
     * @param paginatedUserCoupons the page of user coupons
     * @param today the current date
     */
    private void validateCoupons(Page<UserCoupon> paginatedUserCoupons, LocalDate today) {
        paginatedUserCoupons.forEach(userCoupon -> {
            if (userCoupon.getCoupon().getExpirationDate().isBefore(today)) {
                userCoupon.updateToInvalid();
                userCouponRepository.save(userCoupon);
            }
        });
    }

    /**
     * Filters and converts the valid user coupons to CouponResponseDto objects.
     *
     * @param paginatedUserCoupons the page of user coupons
     * @return a list of valid CouponResponseDto objects
     */
    private List<CouponResponseDto> getValidCoupons(Page<UserCoupon> paginatedUserCoupons) {
        return paginatedUserCoupons.stream()
                .filter(UserCoupon::getIsValid)
                .map(userCoupon -> {
                    Coupon coupon = userCoupon.getCoupon();
                    return CouponResponseDto.of(coupon.getId(), coupon.getName(), coupon.getContent(), coupon.getExpirationDate());
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a new coupon based on the provided request data.
     *
     * <p>This method checks for duplicate coupon names and validates the expiration date.
     * If the coupon name already exists, a DuplicateNameException is thrown.
     * If the expiration date is in the past, an InvalidExpirationDateException is thrown.</p>
     *
     * @param couponRequestDto the coupon request data transfer object
     * @return the created coupon response
     * @throws DuplicateNameException if a coupon with the same name already exists
     * @throws InvalidExpirationDateException if the expiration date is in the past
     * @throws Exception if an internal server error occurs
     */
    @Override
    public CouponResponseDto createCoupon(CouponRequestDto couponRequestDto) {
        Optional<Coupon> coupon =  couponRepository.findByName(couponRequestDto.getName());
        if(coupon.isPresent()) {
            throw new DuplicateNameException(MESSAGE_400_duplicatedCoupon);
        }
        LocalDate today = LocalDate.now();
        if(couponRequestDto.getExpirationDate().isBefore(today)){
            throw new InvalidExpirationDateException(MESSAGE_400_InvalidExpirationDate);
        }
        CouponMapper couponMapper = new CouponMapper();
        Coupon mappedCoupon = couponMapper.mapToCoupon(couponRequestDto);
        couponRepository.save(mappedCoupon);
        return CouponResponseDto.of(mappedCoupon.getId(), mappedCoupon.getName(), mappedCoupon.getContent(), mappedCoupon.getExpirationDate());
    }

    /**
     * Retrieves a list of all available coupons with pagination.
     *
     * @param pageable pagination information
     * @return a paginated list of all coupons
     */
    @Override
    public Page<CouponResponseDto> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        return coupons.map(coupon -> CouponResponseDto.of(coupon.getId(), coupon.getName(), coupon.getContent(), coupon.getExpirationDate()));
    }

    /**
     * Distributes a coupon to a specific user.
     *
     * @param couponId the ID of the coupon to distribute
     * @param userId the ID of the user to receive the coupon
     * @throws NotFoundException if the user or coupon is not found
     */
    @Override
    @Transactional
    public void distributeCouponToUser(Long couponId, Long userId) {
        Users findUser = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_UserNotFound)
        );
        Coupon findCoupon = couponRepository.findById(couponId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_CouponNotFound)
        );
        UserCoupon userCoupon = UserCoupon.createUserCoupon(findUser, findCoupon);
        userCouponRepository.save(userCoupon);
    }

    /**
     * Distributes a coupon to all users with a specific tier.
     *
     * @param couponId the ID of the coupon to distribute
     * @param tier the tier of users to receive the coupon
     * @throws NotFoundException if the coupon is not found
     */
    @Override
    @Transactional
    public void distributeCouponToUsersByTier(Long couponId, Tiers tier) {
        List<Users> findUsers = userRepository.findByTier(tier);
        Coupon findCoupon = couponRepository.findById(couponId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_CouponNotFound)
        );
        assignCoupon(findUsers, findCoupon);
    }

    /**
     * Distributes a coupon to all users.
     *
     * @param couponId the ID of the coupon to distribute
     * @throws NotFoundException if the coupon is not found
     */
    @Override
    @Transactional
    public void distributeCouponToAllUsers(Long couponId) {
        List<Users> findUsers = userRepository.findAll();
        Coupon findCoupon = couponRepository.findById(couponId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_CouponNotFound)
        );
        assignCoupon(findUsers, findCoupon);
    }

    /**
     * Assigns a coupon to a list of users.
     *
     * @param findUsers the list of users to assign the coupon to
     * @param findCoupon the coupon to assign
     */
    private void assignCoupon(List<Users> findUsers, Coupon findCoupon) {
        findUsers.forEach(
                user -> {
                    UserCoupon coupon = UserCoupon.createUserCoupon(user, findCoupon);
                    userCouponRepository.save(coupon);
                }
        );
    }
}
